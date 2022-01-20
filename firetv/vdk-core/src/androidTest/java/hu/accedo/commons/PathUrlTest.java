/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.net.PathUrl;
import java.net.URLEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class PathUrlTest {
    private static final String BASE_URL = "http://ovp-staging.cloud.accedo.tv";

    private static final String PATH_MOVIE = "/movie";
    private static final String PATH_MOVIE_BY_ID = "/movie/:id";
    private static final String PATH_MOVIE_BY_ID_AND_CATEGORY_ID = "/movie/:id/:categoryId"; //Nonexistent in OVP :)
    private static final String PATH_MOVIE_BY_ID_AND_CATEGORY_ID_WITH_NUMBERS = "/movie/:param1/:param2"; //Nonexistent in OVP :)

    @Test
    public void testBasicPath() {
        PathUrl pathUrl = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE);
    }

    @Test
    public void testFixedParam() {
        String movieId = "the-conjuring";

        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addFixedParam(":id", movieId);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID.replaceAll(":id", movieId));
    }

    @Test
    public void testNonEncodedFixedParam() {
        String movieId = "the:conjuring";

        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addFixedParam(":id", movieId);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID.replaceAll(":id", URLEncoder.encode(movieId)));
    }

    @Test
    public void testEncodedFixedParam() {
        String movieId = "the:conjuring";

        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addEscapedFixedParam(":id", movieId);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID.replaceAll(":id", movieId));
    }

    @Test
    public void testFixedParams() {
        String movieId = "the-conjuring";
        String categoryId = "horror";

        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID_AND_CATEGORY_ID)
                .setBaseUrl(BASE_URL)
                .addFixedParam(":id", movieId)
                .addFixedParam(":categoryId", categoryId);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID_AND_CATEGORY_ID.replaceAll(":id", movieId).replaceAll(":categoryId", categoryId));
    }

    @Test
    public void testFixedParamsWithNumbers() {
        String movieId = "the-conjuring";
        String categoryId = "horror";

        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID_AND_CATEGORY_ID_WITH_NUMBERS)
                .setBaseUrl(BASE_URL)
                .addFixedParam(":param1", movieId)
                .addFixedParam(":param2", categoryId);

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID_AND_CATEGORY_ID_WITH_NUMBERS.replaceAll(":param1", movieId).replaceAll(":param2", categoryId));
    }

    @Test
    public void testObjectParam() {
        String movieId = "the-conjuring";
        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addObjectParam(new Movie(movieId));

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID.replaceAll(":id", movieId));
    }

    @Test
    public void testObjectParams() {
        String movieId = "the-conjuring";
        String categoryId = "horror";
        PathUrl pathUrl = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addObjectParam(new Movie(movieId))
                .addObjectParam(new Category(categoryId));

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE_BY_ID.replaceAll(":id", movieId).replaceAll(":categoryId", categoryId));
    }

    @Test
    public void testQueryParams() {
        PathUrl pathUrl = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("pageSize", "100")
                .addQueryParam("pageNumber", "1");

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE + "?pageSize=100&pageNumber=1");
    }

    @Test
    public void testUnescapedQueryParams() {
        PathUrl pathUrl = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("tag", "sálálálá");

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE + "?tag=s%C3%A1l%C3%A1l%C3%A1l%C3%A1");
    }

    @Test
    public void testEscapedQueryParams() {
        PathUrl pathUrl = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addEscapedQueryParam("tag", "s%C3%A1l%C3%A1l%C3%A1l%C3%A1");

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE + "?tag=s%C3%A1l%C3%A1l%C3%A1l%C3%A1");
    }

    @Test
    public void testDuplicatedQueryParams() {
        PathUrl pathUrl = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("pageSize", "100")
                .addQueryParam("pageNumber", "1")
                .addQueryParam("pageSize", "3");

        assertEquals(pathUrl.toString(), BASE_URL + PATH_MOVIE + "?pageSize=3&pageNumber=1");
    }

    @Test
    public void testHashCodeAndEquals() {
        PathUrl pathUrl1 = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("pageSize", "100")
                .addQueryParam("pageNumber", "1");
        PathUrl pathUrl2 = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("pageSize", "100")
                .addQueryParam("pageNumber", "1");
        PathUrl pathUrl3 = new PathUrl(PATH_MOVIE_BY_ID)
                .setBaseUrl(BASE_URL)
                .addObjectParam(new Movie("the-conjuring"))
                .addObjectParam(new Category("horror"));

        assertEquals(pathUrl1.hashCode(), pathUrl2.hashCode());
        assertNotEquals(pathUrl2.hashCode(), pathUrl3.hashCode());

        assertEquals(pathUrl1, pathUrl2);
        assertNotEquals(pathUrl2, pathUrl3);
    }

    @Test
    public void testBaseWithPortInPath() {
        String port = ":8000";
        String movieId = "the-conjuring";

        assertEquals(
                BASE_URL + port + PATH_MOVIE_BY_ID.replaceFirst(":id", movieId),
                new PathUrl(BASE_URL + port + PATH_MOVIE_BY_ID).addFixedParam(":id", movieId).toString()
        );
    }

    @Test
    public void testParseUrl() {
        PathUrl pathUrlBuilt = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("key", "value");

        PathUrl pathUrlParsed = PathUrl.parse(BASE_URL + PATH_MOVIE + "?key=value");

        assertEquals(pathUrlBuilt.toString(), pathUrlParsed.toString());
    }

    @Test
    public void testParseUrlQueryParamsOverride() {
        PathUrl pathUrlBuilt = new PathUrl(PATH_MOVIE)
                .setBaseUrl(BASE_URL)
                .addQueryParam("key", "new");

        PathUrl pathUrlParsed = PathUrl.parse(BASE_URL + PATH_MOVIE + "?key=old&key=new");

        assertEquals(pathUrlBuilt.toString(), pathUrlParsed.toString());
    }

    @Test
    public void testParseUrlNoQueryParams() {
        PathUrl pathUrlBuilt = new PathUrl(PATH_MOVIE).setBaseUrl(BASE_URL);
        PathUrl pathUrlParsed = PathUrl.parse(BASE_URL + PATH_MOVIE);

        assertEquals(pathUrlBuilt.toString(), pathUrlParsed.toString());
    }

    @Test
    public void testParseUrlEncoding() {
        PathUrl pathUrlBuilt = new PathUrl(PATH_MOVIE).setBaseUrl(BASE_URL).addQueryParam("key", "válúé");
        PathUrl pathUrlParsed = PathUrl.parse(BASE_URL + PATH_MOVIE + "?key=v%C3%A1l%C3%BA%C3%A9");

        assertEquals(pathUrlBuilt.toString(), pathUrlParsed.toString());
    }

    @Test
    public void testQueryInBaseUrl() {
        // Prior to 4.0.3, if there's a query in the baseUrl already, PathUrl will wrongly put another "?" while putting it's own query params.
        // This test checks if that behavior is fixed.

        PathUrl pathUrl = new PathUrl("")
                .setBaseUrl("http://www.google.com/search?query=hardcode")
                .addQueryParam("param", "value");

        assertEquals("http://www.google.com/search?query=hardcode&param=value", pathUrl.toString());
    }

    private static class Movie {
        private String id;

        public Movie(String id) {
            this.id = id;
        }
    }

    private static class Category {
        private String categoryId;

        public Category(String categoryId) {
            this.categoryId = categoryId;
        }
    }
}
