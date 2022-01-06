package cz.apigames.betterhud.api;

import cz.apigames.betterhud.api.Displays.Display;
import cz.apigames.betterhud.api.Displays.DisplayType;
import cz.apigames.betterhud.api.Elements.*;
import cz.apigames.betterhud.api.Utils.ExceptionListener;
import cz.apigames.betterhud.api.Utils.Condition;
import cz.apigames.betterhud.api.Utils.Listeners.ListenerRegister;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.InstanceAlreadyExistsException;
import javax.validation.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BetterHudAPI {

    protected static List<ExceptionListener> listeners = new ArrayList<>();
    protected static JavaPlugin plugin;
    protected static boolean PAPI_ENABLED;
    protected static boolean HEX_SUPPORTED;

    protected static HashMap<String, Hud> hudMap = new HashMap<>();

    protected static File FontImagesDirectory;

    protected static HashMap<Player, List<Placeholder>> placeholders = new HashMap<>();

    public static final HashMap<String, FontImageWrapper> fontImageCharacters = new HashMap<>();
    public static final HashMap<Character, String> charactersInternalNames = new HashMap<>();

    private static final String[] HEX_VERSIONS = {"1.16", "1.17", "1.18"};

    /**
     * Constructor for BetterHudAPI class (can be used only once)
     */
    public BetterHudAPI(JavaPlugin plugin) {

        if(BetterHudAPI.plugin == null) {
            BetterHudAPI.plugin = plugin;

            PAPI_ENABLED = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
            checkHexSupport();

            ListenerRegister.registerListeners();

        }

    }

    /* --- EXCEPTION LISTENER --- */

    /**
     * Registers a new ExceptionListener class
     *
     * @param listener   the listener class
     */
    public static void registerExceptionListener(ExceptionListener listener) {
        listeners.add(listener);
    }

    /* --- HUDS --- */

    /**
     * Creates a new hud with specified name
     *
     * @throws InstanceAlreadyExistsException if hud with this name already exists
     * @return New hud instance
     */
    public Hud createHud(String name) throws InstanceAlreadyExistsException {

        if(hudMap.containsKey(name)) {;
            throw new InstanceAlreadyExistsException("Hud with this name already exists! Name of the hud: "+name);
        }

        Hud hud = createHud();
        hudMap.remove(hud.name);
        hud.setName(name);
        hudMap.put(hud.name, hud);

        return hud;
    }

    /**
     * Creates a new hud
     *
     * @return New hud instance
     */
    public Hud createHud() {
        Hud hud = new Hud();
        hud.setName(generateHudName());
        hudMap.put(hud.name, hud);
        return hud;
    }

    /**
     * Unloads the hud from BetterHudAPI
     *
     * @param hudName name of the hud
     */
    public void removeHud(String hudName) {
        Display.getDisplays(hudMap.get(hudName)).forEach(Display::destroy);
        hudMap.get(hudName).elements.clear();
        hudMap.remove(hudName);
    }

    /**
     * Returns hud instance by its internal name
     *
     * @param hudName internal name of this hud (used in config)
     * @return Nullable Hud instance
     */
    public Hud getHud(String hudName) {
        return hudMap.get(hudName);
    }

    /**
     * Returns all loaded huds
     *
     * @return Collection of nullable Hud instances
     */
    public static Collection<Hud> getLoadedHuds() {
        return hudMap.values();
    }

    /**
     * Checks if hud by specified name exists
     *
     * @param hudName   the internal name of the hud (used in config)
     * @return true if hud exists
     */
    public boolean hudExists(String hudName) {

        return hudMap.containsKey(hudName);

    }

    private String generateHudName() {

        int i = 0;
        while(hudMap.containsKey("generatedHud-"+i)) {
            i++;
        }
        return "generatedHud-"+i;

    }

    /* --- FILES --- */

    /**
     * Loads all huds from specified file
     *
     * @param file   the file, from where the huds should be loaded
     * @param override override already loaded huds
     *
     * @return List of error messages
     */
    public List<String> load(File file, boolean override) {

        //JAVAX Validator init
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        List<String> violations = new ArrayList<>();

        if(override) {
            hudMap.clear();
        }

        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(file);

        for(String hudName : yamlFile.getConfigurationSection("huds").getKeys(false) ) {

            try {
                Hud hud = createHud(hudName);

                if(yamlFile.isSet("huds."+hudName+".refresh-interval")) {
                    hud.setRefreshInterval(yamlFile.getInt("huds."+hudName+".refresh-interval"));
                } else {
                    hud.setRefreshInterval(1000);
                }

                //HUD CONDITIONS
                if(yamlFile.isSet("huds."+hudName+".conditions")) {
                    for(String conditionName : yamlFile.getConfigurationSection("huds."+hudName+".conditions").getKeys(false)) {

                        Condition cond = new Condition(yamlFile.getString("huds."+hudName+".conditions."+conditionName));

                        Set<ConstraintViolation<Condition>> conditions_violations = validator.validate(cond);
                        if(conditions_violations.isEmpty()) {
                            hud.addCondition(cond);
                        } else {
                            violations.add("Errors for condition '"+conditionName+"' inside Hud '" + hudName + "':");
                            conditions_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                        }

                    }
                }

                //HUD EVENTS
                if(yamlFile.isSet("huds."+hudName+".toggle-events")) {
                    for(String eventName : yamlFile.getConfigurationSection("huds."+hudName+".toggle-events").getKeys(false)) {

                        try {
                            ToggleEvent toggleEvent;

                            if(yamlFile.isSet("huds."+hudName+".toggle-events."+eventName+".value")) {
                                toggleEvent = new ToggleEvent(ToggleEvent.EventType.valueOf(yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".event")), DisplayType.valueOf(yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".display")), yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".value"), yamlFile.getInt("huds."+hudName+".toggle-events."+eventName+".hide_after"));
                            } else {
                                toggleEvent = new ToggleEvent(ToggleEvent.EventType.valueOf(yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".event")), DisplayType.valueOf(yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".display")), yamlFile.getInt("huds."+hudName+".toggle-events."+eventName+".hide_after"));
                            }

                            Set<ConstraintViolation<ToggleEvent>> event_violations = validator.validate(toggleEvent);
                            if(event_violations.isEmpty()) {
                                hud.addEvent(toggleEvent);
                            } else {
                                violations.add("Errors for ToggleEvent '"+toggleEvent+"' inside Hud '" + hudName + "':");
                                event_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                            }

                        } catch (IllegalArgumentException e) {
                            violations.add("Unknown ToggleEvent: "+yamlFile.getString("huds."+hudName+".toggle-events."+eventName+".event"));
                        }

                        Set<ConstraintViolation<ToggleEvent>> event_violations = validator.validate(toggleEvent);
                        if(event_violations.isEmpty()) {
                            hud.addEvent(toggleEvent);
                        } else {
                            violations.add("Errors for ToggleEvent '"+toggleEvent+"' inside Hud '" + hudName + "':");
                            event_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                        }

                    }
                }

                for(String elementName : yamlFile.getConfigurationSection("huds."+hudName+".elements").getKeys(false)) {

                    Element element = null;
                    int x,y,scale;
                    Alignment align = Alignment.LEFT;

                    x = yamlFile.getInt("huds."+hudName+".elements."+elementName+".position-x");
                    y = yamlFile.getInt("huds."+hudName+".elements."+elementName+".position-y");
                    scale = yamlFile.getInt("huds."+hudName+".elements."+elementName+".scale");

                    //ALIGN
                    if(yamlFile.isSet("huds."+hudName+".elements."+elementName+".align")) {
                        align = Alignment.get(yamlFile.getString("huds."+hudName+".elements."+elementName+".align"));
                    }

                    String type = yamlFile.getString("huds."+hudName+".elements."+elementName+".type");

                    //TEXT
                    if(type.equalsIgnoreCase("TEXT")) {
                        element = new TextElement(elementName, x,y,scale,yamlFile.getString("huds."+hudName+".elements."+elementName+".value"));
                        element.setAlign(align);
                    }

                    //PLAIN TEXT
                    else if(type.equalsIgnoreCase("PLAIN_TEXT")) {
                        element = new PlainTextElement(elementName, x, yamlFile.getString("huds."+hudName+".elements."+elementName+".value"));
                        element.setAlign(align);
                    }

                    //IMAGE
                    else if(type.equalsIgnoreCase("IMAGE")) {
                        element = new ImageElement(elementName, x,y,scale,yamlFile.getString("huds."+hudName+".elements."+elementName+".texture-path"));
                    }

                    //INTEGER
                    else if(type.equalsIgnoreCase("INTEGER")) {
                        element = new IntegerElement(elementName, x,y,scale,yamlFile.getString("huds."+hudName+".elements."+elementName+".value"));
                        element.setAlign(align);
                    }

                    //ERROR
                    else {
                        violations.add("Configuration errors for Element '" + elementName + "' inside hud '" + hudName + "':");
                        violations.add("╚ Element type '" + type + "' is not valid!");
                    }

                    if(element != null) {

                        //ELEMENT CONDITIONS
                        if(yamlFile.isSet("huds."+hudName+".elements."+elementName+".conditions")) {
                            for(String conditionName : yamlFile.getConfigurationSection("huds."+hudName+".elements."+elementName+".conditions").getKeys(false)) {

                                Condition cond = new Condition(yamlFile.getString("huds."+hudName+".elements."+elementName+".conditions."+conditionName));

                                Set<ConstraintViolation<Condition>> conditions_violations = validator.validate(cond);
                                if(conditions_violations.isEmpty()) {
                                    element.addCondition(cond);
                                } else {
                                    violations.add("Errors for "+ elementName +"'s condition '"+conditionName+"' inside Hud '" + hudName + "':");
                                    conditions_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                                }

                            }
                        }

                        Set<ConstraintViolation<Element>> element_violations = validator.validate(element);
                        if(element_violations.isEmpty()) {
                            hud.addElement(element);
                        } else {
                            violations.add("Configuration errors for Element '" + elementName + "' inside hud '" + hudName + "':");
                            element_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                        }

                    }

                }

                Set<ConstraintViolation<Hud>> hud_violations = validator.validate(hud);
                if(hud_violations.isEmpty()) {
                    hudMap.put(hudName, hud);
                } else {
                    violations.add("Configuration errors for Hud '" + hudName + "':");
                    hud_violations.forEach(violation -> violations.add("╚ "+violation.getMessage()));
                }



            } catch (InstanceAlreadyExistsException e) {
                BetterHudAPI.getListeners().forEach(exceptionListener -> exceptionListener.onException(e));
            }


        }
        ToggleCommand.registerCommands();
        return violations;
    }

    /**
     * Unloads all the loaded huds
     */
    public void unload() {

        hudMap.forEach((s, hud) -> hudMap.get(s).elements.clear());

        Display.destroyAll();
        hudMap.clear();

    }

    /**
     * Saves loaded huds to the specified file
     *
     * @param file   the file, where the huds should be saved
     * @return true if task was successful
     */
    public boolean save(File file) {

        try {

            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(file);

            hudMap.values().forEach(hud -> {

                yamlFile.set("huds."+hud.getName()+".refresh-interval", hud.getRefreshInterval());
                hud.elements.forEach(element -> {

                    yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".type", element.getConfigName());
                    yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".position-x", element.getX());
                    yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".position-y", element.getY());
                    yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".scale", element.getScale());
                    yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".align", element.getAlign().toString());

                    if(element instanceof ImageElement) {
                        yamlFile.set("huds." + hud.getName() + ".elements." + element.getName() + ".texture-path", ((ImageElement) element).getImageName());
                    } else if(element instanceof IntegerElement) {
                        yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".value", element.getValue());
                    } else if(element instanceof PlainTextElement) {
                        yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".value", element.getValue());
                    } else if(element instanceof TextElement) {
                        yamlFile.set("huds."+hud.getName()+".elements."+element.getName()+".value", element.getValue());
                    }

                });

            });

            yamlFile.save(file);
            return true;

        } catch (IOException e) {
            BetterHudAPI.getListeners().forEach(exceptionListener -> exceptionListener.onException(e));
            return false;
        }

    }

    /**
     * Asynchronously generates files inside ItemsAdder itemspack folder, where the font images are stored
     *
     * @param charactersFile YAML file where characters settings is stored
     * @param directory   path to the destination directory (must be inside itemspack folder)
     *
     * @return Future boolean, true if the task was successful
     */
    public Future<Boolean> generateFontImageFiles(File charactersFile, File directory) {

        FontImagesDirectory = directory;
        CompletableFuture<Boolean> success;

        //USE OF DAEMON ASYNC THREAD
        success = CompletableFuture.supplyAsync( () -> {
            HashMap<String, YamlConfiguration> namespaceFiles = new HashMap<>();
            HashMap<String, String> charsets = new HashMap<>();
            HashMap<String, String> charactersPath = new HashMap<>();

            try {

                if(!directory.exists()) {
                    directory.mkdirs();
                }
                if(directory.isDirectory()) {

                    //DELETE EXISTING FILES
                    if (directory.listFiles() != null) {

                        for (File child : directory.listFiles()) {
                            child.delete();
                        }
                    }

                }

                if(charactersFile.exists()) {

                    YamlConfiguration charFile = YamlConfiguration.loadConfiguration(charactersFile);

                    //GROUP LOOP
                    for(String group : charFile.getConfigurationSection("groups").getKeys(false)) {

                        String namespace = charFile.getString("groups."+group+".namespace");
                        String path = charFile.getString("groups."+group+".path");
                        List<String> elements = charFile.getStringList("groups."+group+".load-for-elements");
                        StringBuilder charBuilder = new StringBuilder();
                        charFile.getConfigurationSection("groups."+group+".characters").getKeys(false).forEach(
                                character -> {

                                    if(character.length() > 0) {

                                        if(character.equals(":dot:")) {
                                            charactersPath.put(".", namespace+":"+path+charFile.getString("groups."+group+".characters.:dot:"));
                                            charactersInternalNames.put(".".charAt(0), charFile.getString("groups."+group+".characters.:dot:").split("\\.")[0]);
                                            charBuilder.append(".");
                                        } else {
                                            charactersPath.put(character, namespace+":"+path+charFile.getString("groups."+group+".characters."+character));
                                            charactersInternalNames.put(character.charAt(0), charFile.getString("groups."+group+".characters."+character).split("\\.")[0]);
                                            charBuilder.append(character);
                                        }

                                    }
                                }
                        );

                        //CHARSETS
                        for(String element : elements) {

                            if(charsets.containsKey(element)) {
                                charBuilder.append(charsets.get(element));
                            }

                            charsets.put(element, charBuilder.toString());

                        }

                        //NAMESPACE FILE
                        File namespaceFile = new File(directory, namespace+"-font_images.yml");
                        if(!namespaceFile.exists()) {
                            namespaceFile.getParentFile().mkdirs();
                            namespaceFile.createNewFile();

                            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(namespaceFile);
                            yaml.set("info.namespace", namespace);

                            namespaceFiles.put(namespaceFile.getName(), yaml);
                        }


                    }

                    hudMap.values().forEach(hud -> {

                        for (Element element : hud.elements) {

                            if(charsets.containsKey(element.getConfigName())) {

                                String charSet = charsets.get(element.getConfigName());

                                for(char ch : charSet.toCharArray()) {

                                    String path = charactersPath.get(String.valueOf(ch));

                                    //CHECK FOR OPTIONS
                                    int y_offset = 0;
                                    if(path.contains(";y-offset=")) {
                                        y_offset = Integer.parseInt(path.split(";y-offset=")[1]);
                                        path = path.split(";y-offset=")[0];
                                    }

                                    String fileName = path.split(":")[0]+"-font_images.yml";
                                    String name = MessageUtils.getCharNameFromPath(path) + "-" + element.getY() + "_" + element.getScale();

                                    namespaceFiles.get(fileName).set("font_images."+name+".path", path.split(":")[1]);
                                    namespaceFiles.get(fileName).set("font_images."+name+".y_position", element.getY()+y_offset);
                                    namespaceFiles.get(fileName).set("font_images."+name+".scale_ratio", element.getScale());
                                }
                            }
                            //CHECK FOR NON-CHARACTER ELEMENTS
                            else if(element instanceof ImageElement) {

                                File namespaceFile = new File(directory, ((ImageElement) element).getNamespace()+"-font_images.yml");
                                if(!namespaceFile.exists()) {
                                    namespaceFile.getParentFile().mkdirs();
                                    try {
                                        namespaceFile.createNewFile();
                                    } catch (IOException e) {
                                        BetterHudAPI.getListeners().forEach(exceptionListener -> exceptionListener.onException(e));
                                    }

                                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(namespaceFile);
                                    yaml.set("info.namespace", ((ImageElement) element).getNamespace());

                                    namespaceFiles.put(namespaceFile.getName(), yaml);
                                }

                                String fileName = ((ImageElement) element).getNamespace()+"-font_images.yml";
                                String name = ((ImageElement) element).getImageName().split(":")[1] + "-" + element.getY() + "_" + element.getScale();

                                namespaceFiles.get(fileName).set("font_images."+name+".path", ((ImageElement) element).getImagePath());
                                namespaceFiles.get(fileName).set("font_images."+name+".y_position", element.getY());
                                namespaceFiles.get(fileName).set("font_images."+name+".scale_ratio", element.getScale());

                            }

                        }

                    });

                    for(String fileName : namespaceFiles.keySet()) {

                        namespaceFiles.get(fileName).save(new File(directory, fileName));

                    }
                    return true;

                }
            } catch (NullPointerException | IOException e) {
                BetterHudAPI.getListeners().forEach(exceptionListener -> exceptionListener.onException(e));
                return false;
            }
            return false;
        });

        return success;

    }

    /**
     * @return plugin instance that initialized BetterHudAPI
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return true if PlaceholderAPI is loaded and enabled
     */
    public static boolean isPapiEnabled() {
        return PAPI_ENABLED;
    }

    /**
     * @return true if hex colors are supported on current server version
     */
    public static boolean isHexSupported() {
        return HEX_SUPPORTED;
    }

    /**
     * @return list of loaded ExceptionListener classes (this is for internal use only)
     */
    public static List<ExceptionListener> getListeners() {
        return listeners;
    }

    /**
     * @return the directory where are stored all namespace configuration files
     */
    public static File getFontImagesDirectory() {
        return FontImagesDirectory;
    }

    private static void checkHexSupport() {

        String curVer = Bukkit.getServer().getVersion();

        for(String ver : HEX_VERSIONS) {
            if(curVer.contains(ver)) {
                HEX_SUPPORTED = true;
            }
        }
        HEX_SUPPORTED = false;

    }

    /* --- PLACEHOLDERS --- */

    /**
     * Returns the set of placeholders for specified player
     *
     * @param player the player
     *
     * @return the set of placeholders
     */
    public static List<Placeholder> getPlaceholders(Player player) {
        return placeholders.get(player);
    }

    /**
     * Set placeholder for the player
     *
     * @param player   the player
     * @param placeholder the placeholder
     */
    public static void setPlaceholder(Player player, Placeholder placeholder) {
        List<Placeholder> placeholderSet = new ArrayList<>();
        if(placeholders.containsKey(player)) {
            placeholderSet = placeholders.get(player);
        }
        placeholderSet.add(placeholder);
        placeholders.put(player, placeholderSet);
    }

    /**
     * Set multiple placeholders for the player (this overrides current placeholders)
     *
     * @param player   the player
     * @param placeholderSet the Set of placeholders
     */
    public static void setPlaceholders(Player player, List<Placeholder> placeholderSet) {
        placeholders.put(player, placeholderSet);
    }

    /**
     * Clears all placeholders for the player
     *
     * @param player   the player
     */
    public static void clearPlaceholders(Player player) {
        placeholders.remove(player);
    }

}
