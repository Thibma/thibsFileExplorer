package fr.thibma.thibsfileexplorer.models

// File qui est un Item
data class File(
    val name: String,
    val path: String
) : Item