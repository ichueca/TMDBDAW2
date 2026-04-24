/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.dao;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.zabalburu.seriesjpa.tmdb.dto.TmdbSerieDTO;
import org.zabalburu.seriesjpa.tmdb.util.TmdbConfig;

public class TmdbClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<TmdbSerieDTO> buscarSeries(String texto) {
        try {
            String query = URLEncoder.encode(texto, StandardCharsets.UTF_8);
            
            String url = TmdbConfig.getBaseUrl()
                    + "/search/tv?api_key=" + TmdbConfig.getApiKey()
                    + "&language=es-ES"
                    + "&query=" + query;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Error al buscar series en TMDb. Código HTTP: " + response.statusCode());
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray results = json.getJSONArray("results");

            List<TmdbSerieDTO> series = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                TmdbSerieDTO dto = new TmdbSerieDTO(
                        item.getInt("id"),
                        item.optString("name", ""),
                        item.optString("overview", ""),
                        parseFecha(item.optString("first_air_date", "")),
                        construirPosterUrl(item.optString("poster_path", null)),
                        null);

                series.add(dto);
            }

            return series;

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Error al conectar con TMDb", e);
        }
    }

    public TmdbSerieDTO obtenerDetalleSerie(Integer tmdbId) {
        try {
            String url = TmdbConfig.getBaseUrl()
                    + "/tv/" + tmdbId
                    + "?api_key=" + TmdbConfig.getApiKey()
                    + "&language=es-ES";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Error al obtener detalle de serie en TMDb. Código HTTP: " + response.statusCode());
            }

            JSONObject json = new JSONObject(response.body());

            JSONArray genres = json.optJSONArray("genres");
            List<String> nombresGeneros = new ArrayList<>();
            if (genres != null) {
                for (int i = 0; i < genres.length(); i++) {
                    JSONObject genero = genres.getJSONObject(i);
                    nombresGeneros.add(genero.getString("name"));
                }
            }

            TmdbSerieDTO dto = new TmdbSerieDTO(
                    json.getInt("id"),
                    json.optString("name", ""),
                    json.optString("overview", ""),
                    parseFecha(json.optString("first_air_date", "")),
                    construirPosterUrl(json.optString("poster_path", null)),
                    nombresGeneros);

            return dto;

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Error al conectar con TMDb", e);
        }
    }

    private LocalDate parseFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return LocalDate.parse(texto);
    }

    private String construirPosterUrl(String posterPath) {
        if (posterPath == null || posterPath.isBlank()) {
            return null;
        }
        return TmdbConfig.getImageBaseUrl() + posterPath;
    }
}

