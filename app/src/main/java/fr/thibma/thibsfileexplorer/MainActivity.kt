package fr.thibma.thibsfileexplorer

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import fr.thibma.thibsfileexplorer.models.File
import fr.thibma.thibsfileexplorer.models.Folder
import fr.thibma.thibsfileexplorer.adapter.FolderFileAdapter
import fr.thibma.thibsfileexplorer.models.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var textViewPath: TextView
    private lateinit var folderFileList: List<Item>
    private lateinit var pathFile: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vérification des permissions + de la version d'Android
        val permissions = android.Manifest.permission.READ_EXTERNAL_STORAGE
        // Si la version d'android est inférieure à Android 10 -> c'est good
        when (Build.VERSION.SDK_INT) {
            in Int.MIN_VALUE..28 -> {
                // On vérifie les permissions accordées
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(this, permissions) -> {
                        // On peut commencer à utiliser l'application
                        permissionGranted();
                    }
                    else -> {
                        // On demande la permission à l'utilisateur
                        requestPermissions(arrayOf(permissions), 0)
                    }
                }
            }
            else -> {
                Toast.makeText(
                    this,
                    "Application uniquement compatible avant Android 9 !",
                    Toast.LENGTH_LONG
                ).show()
                finish()

            }
        }
    }

    // Réception de l'information de la permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // On reçoit l'info de la permission
        when (requestCode) {
            0 -> {
                // Si l'user est ok on passe à la suite, sinon on ferme l'app
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted()
                } else {
                    finish()
                    exitProcess(0)
                }
                return
            }
            else -> {
                finish()
                exitProcess(0)
            }
        }
    }

    // Permissions accordées, on peut commencer à utiliser l'app
    private fun permissionGranted() {
        // On cherche le chemin absolu de l'external Storage
        pathFile = Environment.getExternalStorageDirectory().absolutePath

        // Affichage du chemin de manière plus optimisée pour l'utilisateur
        textViewPath = findViewById(R.id.textViewPath)
        textViewPath.text = pathFile.replace("/storage/emulated/0", "/")

        // On va créer la recyclerView
        setRecyclerView()
    }

    // Lorsque le bouton back est appuyé
    override fun onBackPressed() {
        // Si on est à la racine, on quitte l'app
        if (pathFile == Environment.getExternalStorageDirectory().absolutePath) {
            super.onBackPressed()
        }
        // Sinon on revient au parent du dossier
        else {
            val parentFile = java.io.File(pathFile).parentFile
            pathFile = parentFile.path
            if (pathFile == Environment.getExternalStorageDirectory().absolutePath) {
                textViewPath.text = pathFile.replace("/storage/emulated/0", "/")
            } else {
                textViewPath.text = pathFile.removePrefix("/storage/emulated/0")
            }
            setRecyclerView()
        }
    }

    // Création de la RecyclerView
    private fun setRecyclerView() {
        // On va chercher la liste des fichiers du répertoire actuel
        val fileList = getFilesFromPath(pathFile)

        // On transforme cette liste de fichiers en liste d'"Items"
        folderFileList = getListItemFromFiles(fileList)

        // On créer l'Adapter du recycler View
        val folderFileAdapter = FolderFileAdapter(folderFileList)

        // On vérifie que le répertoire n'est pas vide
        if (folderFileList.isEmpty()) {
            emptyFolderLayout.visibility = View.VISIBLE
        } else {
            emptyFolderLayout.visibility = View.INVISIBLE
        }

        // On créer l'adapter pour le recycler view
        recyclerViewFolderFile.adapter = folderFileAdapter
        recyclerViewFolderFile.layoutManager = LinearLayoutManager(this)
        recyclerViewFolderFile.setHasFixedSize(true)

        // Lorsque l'on clique sur un Item du RecyclerView
        folderFileAdapter.onItemClick = { pos, view ->
            // On vérifie si c'est un Répertoire ou un Fichier
            when (folderFileList[pos]) {
                // On rentre dans le répertoire
                is Folder -> {
                    pathFile = (folderFileList[pos] as Folder).path
                    textViewPath.text = pathFile.removePrefix("/storage/emulated/0")
                    setRecyclerView()
                }
                // On affiche les applications pouvant ouvrir ce fichier
                is File -> {
                    println("test")
                    val intent =
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_STREAM,
                                Uri.parse((folderFileList[pos] as File).path)
                            )
                            type = "*/*"
                        }
                    val chooser = Intent.createChooser(intent, "Ouvrir avec :")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(chooser)
                }
            }
        }
    }

    // Fonction récupérant la liste des fichiers présent dans le répertoire mis en entrée
    private fun getFilesFromPath(path: String): List<java.io.File> {
        val list = ArrayList<java.io.File>()
        val files = java.io.File(path).listFiles().sortedArray()
        for (file: java.io.File in files) {
            list.add(file)
        }
        return list
    }

    // Fonction récupérant la liste des "Item" en triant la liste d'"Item"
    private fun getListItemFromFiles(files: List<java.io.File>): List<Item> {
        val list = ArrayList<Item>()
        for (i in 0 until files.count()) {
            if (files[i].isDirectory) {
                val folder = Folder(files[i].name, files[i].path)
                list += folder
            }
        }
        for (i in 0 until files.count()) {
            if (files[i].isFile) {
                val file = File(files[i].name, files[i].path)
                list += file
            }
        }
        return list
    }
}

