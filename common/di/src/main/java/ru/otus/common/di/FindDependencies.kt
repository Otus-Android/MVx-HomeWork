package ru.otus.common.di

import android.content.Context

interface Dependencies

interface DependenciesProvider {
    fun getDependencies(): Dependencies
}

inline fun<reified T: Dependencies> Context.findDependencies(): T {
    return (applicationContext as DependenciesProvider).getDependencies() as T
}
