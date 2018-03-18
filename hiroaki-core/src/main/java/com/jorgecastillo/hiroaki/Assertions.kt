package com.jorgecastillo.hiroaki

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

infix fun <T> T.eq(other: T) {
    assertThat(this, `is`(other))
}
