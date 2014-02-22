package com.moreapps;

import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;

public class SimplifiedModel extends HashMap<String, String> {

    private final Log log;

    public SimplifiedModel(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public void simplify(String key) {
        if (key == null) {
            return;
        }
        if (!this.containsKey(key)) {
            try {
                if (isListType(key)) {
                    String className = key.substring("List[".length(), key.length() - 1);
                    Class<?> clazz = Class.forName(className);
                    String simplifiedName = "List[" + clazz.getSimpleName() + "] ";
                    getLog().info("  " + key + " ==> " + simplifiedName);
                    this.put(key, simplifiedName);
                } else if (isMapType(key)) {
                    String[] classNames = key.substring("Map[".length(), key.length() - 1).split(",");
                    String simplifiedClassname = "";
                    for (String className : classNames) {
                        try {
                            if (!simplifiedClassname.isEmpty()) {
                                simplifiedClassname += ",";
                            }
                            Class<?> clazz = Class.forName(className);
                            simplifiedClassname += clazz.getSimpleName();
                        } catch (ClassNotFoundException e) {
                            simplifiedClassname += className;
                        }
                    }
                    String simplifiedName = "Map[" + simplifiedClassname + "] ";
                    getLog().info("  " + key + " ==> " + simplifiedName);
                    this.put(key, simplifiedName);
                } else {
                    Class<?> clazz = Class.forName(key);
                    String simplifiedName = clazz.getSimpleName();
                    getLog().info("  " + key + " ==> " + simplifiedName);
                    this.put(key, simplifiedName);
                }
            } catch (ClassNotFoundException e) {
                this.put(key, key);
            }
        }
    }

    private boolean isListType(String key) {
        return key.startsWith("List[") && key.endsWith("]");
    }

    private boolean isMapType(String key) {
        return key.startsWith("Map[") && key.endsWith("]");
    }

}
