package ru.otus.common.di

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.findDependencies(): T {
    return ((activity?.applicationContext as DependenciesProvider).getDependencies() as T)
}

interface Dependencies

interface DependenciesProvider {
    fun getDependencies(): Dependencies
}