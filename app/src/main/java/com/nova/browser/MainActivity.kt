package com.nova.browser

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import java.net.URLEncoder

// Nova browser: WebView browser, Google-only search, My Private grid, wallpapers.
class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var searchBox: EditText
    private lateinit var db: NovaDatabase

    // Amazing pictures for the home page (Unsplash stock images).
    private val wallpapers = listOf(
        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
        "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(applicationContext, NovaDatabase::class.java, "nova-db")
            .allowMainThreadQueries().build()
        webView = findViewById(R.id.webView)
        searchBox = findViewById(R.id.searchBox)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        findViewById<Button>(R.id.searchButton).setOnClickListener { go() }
        findViewById<Button>(R.id.myPrivateButton).setOnClickListener { showMyPrivate() }
        findViewById<Button>(R.id.addToMyPrivateButton).setOnClickListener { addCurrentToMyPrivate() }
        // Default home: Google search page with Nova branding in the bar above.
        webView.loadUrl("https://www.google.com")
    }

    private fun go() {
        val text = searchBox.text.toString().trim()
        if (text.isEmpty()) return
        // Google-only search: URL -> site, otherwise Google search.
        val url = if (text.contains(".") && !text.contains(" ")) {
            if (text.startsWith("http")) text else "https://$text"
        } else {
            "https://www.google.com/search?q=" + URLEncoder.encode(text, "UTF-8")
        }
        webView.loadUrl(url)
    }

    private fun addCurrentToMyPrivate() {
        val url = webView.url ?: return
        val name = webView.title ?: url
        // MANDATORY rule: save ONLY in My Private, never create a phone home-screen shortcut here.
        db.myPrivateDao().insert(MyPrivateItem(name = name, url = url))
        Toast.makeText(this, "My Private me save ho gaya: $name", Toast.LENGTH_SHORT).show()
    }

    private fun showMyPrivate() {
        val items = db.myPrivateDao().getAll()
        val names = items.joinToString("\n") { "${it.name} - ${it.url}" }
        Toast.makeText(this, if (names.isEmpty()) "My Private khaali hai" else names, Toast.LENGTH_LONG).show()
        // In the full UI this opens the My Private grid (RecyclerView) using item_my_private.xml.
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
