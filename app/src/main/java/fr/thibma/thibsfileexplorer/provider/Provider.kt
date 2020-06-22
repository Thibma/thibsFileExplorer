package fr.thibma.thibsfileexplorer.provider

import android.annotation.SuppressLint
import androidx.core.content.FileProvider

@SuppressLint("Registered")

// Provider (je n'ai pas très bien compris pourquoi on doit faire ça,
// j'ai cru comprendre que c'était à cause des dernières versions d'android..)
class Provider: FileProvider()