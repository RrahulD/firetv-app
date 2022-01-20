package hu.accedo.commons.net.restclient.implementations;

import androidx.annotation.NonNull;

import hu.accedo.commons.net.restclient.Response;
import hu.accedo.commons.net.restclient.RestClient;

/**
 * Defines how a RestClient should fetch it's Response based on its input parameters.
 */
public interface Implementation {
    /**
     * Should create a Response out of the given RestClient's input parameters. Should never throw. On error, should produce a:
     * new Response(url, exception)
     *
     * @param restClient the RestClient to work off from.
     * @return a Response object, that is never null.
     */
    @NonNull
    Response fetchResponse(@NonNull RestClient restClient);
}
