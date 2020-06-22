package fr.thibma.thibsfileexplorer.models

// Folder qui est un Item
data class Folder(
    val name: String,
    val path: String
) : Item