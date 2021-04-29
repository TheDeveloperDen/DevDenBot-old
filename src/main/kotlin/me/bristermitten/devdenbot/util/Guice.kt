package me.bristermitten.devdenbot.util

import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.TypeLiteral

inline fun <reified T> Injector.getInstance() = getInstance(Key.get(object : TypeLiteral<T>() {}))
