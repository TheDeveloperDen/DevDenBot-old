package net.developerden.devdenbot.inject

/**
 * This is just a decorator, but it's a good practice to add it to types fetched by reflection
 * As it means that IDE's can suppress "class unused" warnings for the classes
 */
@Target(AnnotationTarget.CLASS)
annotation class Used
