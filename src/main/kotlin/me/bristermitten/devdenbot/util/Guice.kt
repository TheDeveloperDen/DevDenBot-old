package me.bristermitten.devdenbot.util

import com.google.inject.Injector

inline fun <reified T> Injector.getInstance() = getInstance(T::class.java)
