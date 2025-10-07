package com.davidmurray.omada.data.remote

/**
 * [Exception] specific to the error codes & messages that return from the Flickr API
 * NOTE: They can vary between function calls where codes are the same, but messages are not
 */
class FlickrException(
    val code: Int?,
    override val message: String
) : Exception()