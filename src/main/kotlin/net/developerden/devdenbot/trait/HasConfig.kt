package net.developerden.devdenbot.trait

import net.developerden.devdenbot.serialization.DDBConfig

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
