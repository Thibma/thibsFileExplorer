package fr.thibma.thibsfileexplorer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.thibma.thibsfileexplorer.R
import fr.thibma.thibsfileexplorer.models.File
import fr.thibma.thibsfileexplorer.models.Folder
import fr.thibma.thibsfileexplorer.models.Item
import kotlin.IllegalArgumentException

// Adapter pour la Recycler view
class FolderFileAdapter(private val item : List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Lorsque l'on clique sur un Item de la liste
    public var onItemClick: ((pos: Int, view: View) -> Unit)? = null

    // View Holder pour les Répertoire
    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Surcharge lors d'un clic
        override fun onClick(v: View?) {
            if (v != null) {
                onItemClick?.invoke(adapterPosition, v)
            }
        }

        // On récupère le texte affiché dans le layout
        val textFolder: TextView = itemView.findViewById(R.id.folderTextView)

        init {
            itemView.setOnClickListener(this)
        }
    }

    // View Holder pour les Fichiers
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Surcharge lors d'un clic
        override fun onClick(v: View?) {
            if (v != null) {
                onItemClick?.invoke(adapterPosition, v)
            }
        }

        // On récupère le texte affiché dans le layout
        val textFile: TextView = itemView.findViewById(R.id.fileTextView)

        init {
            itemView.setOnClickListener(this)
        }
    }

    // Nombres d'objets dans la liste du RecyclerView
    override fun getItemCount() = item.count()

    // On vérifie quel type d'Item c'est
    override fun getItemViewType(position: Int): Int =
        when (item[position]){
            is Folder -> TYPE_FOLDER
            is File -> TYPE_FILE
            else -> throw IllegalArgumentException()
        }

    // A la création du ViewHolder, on regarde quel type d'item on a repéré dans le getItemViewType
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_FOLDER -> FolderViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false))

        TYPE_FILE -> FileViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false))

        else -> throw IllegalArgumentException()
    }

    // On va bind les données en fonction dy type d'Item
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            TYPE_FOLDER -> onBindFolder(holder, item[position] as Folder)
            TYPE_FILE -> onBindFile(holder, item[position] as File)
            else -> throw IllegalArgumentException()
    }

    // En fonction du Répertoire
    private fun onBindFolder(holder: RecyclerView.ViewHolder, item: Folder) {
        (holder as FolderViewHolder).textFolder.text = item.name
    }

    // En fonction du Fichier
    private fun onBindFile(holder: RecyclerView.ViewHolder, item: File) {
        (holder as FileViewHolder).textFile.text = item.name
    }

    // Permet de faire une vérif plus simplement
    companion object {
        private const val TYPE_FOLDER = 0
        private const val TYPE_FILE = 1
    }

}