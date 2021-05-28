package me.bristermitten.devdenbot.trait

import me.bristermitten.devdenbot.serialization.DDBConfig

/**
 * Any type that has a variable of type [DDBConfig]
 * This allows access to some config related extension functions
 */
interface HasConfig {
    /**
     * The config instance
     */
    val ddbConfig: DDBConfig
}
