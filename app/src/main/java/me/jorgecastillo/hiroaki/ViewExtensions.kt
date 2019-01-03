package me.jorgecastillo.hiroaki

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.load(url: String) {
  Picasso.get().load(url).into(this)
}
