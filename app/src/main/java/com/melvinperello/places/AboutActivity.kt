package com.melvinperello.places

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

/**
 * Hiiii. my first kotlin class <3 hahaha
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.about)

        val versionElement: Element = Element()
        versionElement.setTitle("Version 6.2")

        var aboutPage: View = AboutPage(this)
                .isRTL(false)
                .setDescription("Places is an app created for fun.")
                .setImage(R.mipmap.ic_launcher)
                .addItem(versionElement)
                .addGroup("Connect with us")
                .addEmail("elmehdi.sakout@gmail.com")
                .addWebsite("http://medyo.github.io/")
                .addFacebook("the.medy")
                .addTwitter("medyo80")
                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addGitHub("medyo")
                .addInstagram("medyo80")
                .create()

        setContentView(aboutPage)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish();
        return true;
    }
}
