package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.NetworkDto

/**
 * Creates a JsonBody object with an inlined json body and the network DTO class it maps to.
 *
 * @param jsonBody expected inlined json body.
 * @param type expected network dto that the body is able to map to.
 */
fun inlineBody(jsonBody: String, type: NetworkDto): JsonBody =
        JsonBody(jsonBody, type)

/**
 * Creates a JsonBodyFile object with a json resource file name and the network DTO class it maps
 * to.
 *
 * @param jsonBodyResFile json resource file name containing the expected json.
 * @param type expected network dto that the body is able to map to.
 */
fun fileBody(jsonBodyResFile: String, type: NetworkDto): JsonBodyFile =
        JsonBodyFile(jsonBodyResFile, type)

data class JsonBody(val jsonBody: String, val type: NetworkDto)
data class JsonBodyFile(val jsonBodyResFile: String, val type: NetworkDto)
