package com.filip.versu.service.impl;

import com.filip.versu.entity.dto.GeocodeResultDTO;
import com.filip.versu.entity.model.GoogleLocation;
import com.filip.versu.service.GoogleMapsService;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class GoogleMapsServiceImpl implements GoogleMapsService {


    private String googleAPIKey;

    @Autowired
    public GoogleMapsServiceImpl(Environment env) {
        googleAPIKey = env.getProperty("google.apikey");
    }

    @Override
    public GoogleLocation findByGoogleID(GoogleLocation location) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://maps.googleapis.com/maps/api/geocode/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("place_id", location.getGoogleID())
                .queryParam("key", googleAPIKey)
                .queryParam("language", "en");

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        HttpEntity<GeocodeResultDTO> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                GeocodeResultDTO.class);

        GeocodeResultDTO geocodeResult = response.getBody();

        if(geocodeResult.getStatus().equals("OK")) {
            if(geocodeResult.getResults() != null && geocodeResult.getResults().size() > 0) {

                location = geoCodeResultToAbsLocationAdapter(geocodeResult, location);
                String formattedAddress = location.getName();
                formattedAddress = formattedAddress.replaceAll("[0-9]", "");//removing digits for street number...
                formattedAddress = formattedAddress.replaceAll(" ,", ",");//clearing the result

                location.setName(formattedAddress);

                //cityLocation represents city of @location
                GoogleLocation cityLocation = new GoogleLocation();
                cityLocation.setGoogleID(location.getGoogleID());
                cityLocation.setLatitude(location.getLatitude());
                cityLocation.setLongitude(location.getLongitude());
                cityLocation = findLocalityByLatLng(cityLocation);

                location.setCityName(cityLocation.getCityName());
                location.setCityGoogleId(cityLocation.getCityGoogleId());
            }
        } else {
            //this should not happen, because it's coded by google's id, only if the google's id is removed
            location = findLocalityByLatLng(location);
        }
        return location;
    }

    @Override
    public GoogleLocation findLocalityByLatLng(GoogleLocation location) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://maps.googleapis.com/maps/api/geocode/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("latlng", location.getLatitude()+","+location.getLongitude())
                .queryParam("key", googleAPIKey)
                .queryParam("language", "en")
                .queryParam("result_type", "locality");

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        HttpEntity<GeocodeResultDTO> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                GeocodeResultDTO.class);

        GeocodeResultDTO geocodeResult = response.getBody();

        if(geocodeResult.getStatus().equals("OK")) {
            location = geoCodeResultToAbsLocationAdapter(geocodeResult, location);

            location.setCityGoogleId(location.getGoogleID());
            location.setCityName(location.getName());

        } else {
            location = findAdminAreaByLatLng(location);
        }

        return location;
    }

    @Override
    public GoogleLocation findAdminAreaByLatLng(GoogleLocation location) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://maps.googleapis.com/maps/api/geocode/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("latlng", location.getLatitude()+","+location.getLongitude())
                .queryParam("key", googleAPIKey)
                .queryParam("language", "en")
                .queryParam("result_type", "administrative_area_level_1");

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        HttpEntity<GeocodeResultDTO> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                GeocodeResultDTO.class);

        GeocodeResultDTO geocodeResult = response.getBody();

        if(geocodeResult.getStatus().equals("OK")) {
            location = geoCodeResultToAbsLocationAdapter(geocodeResult, location);
        } else {
            location.setName(GoogleLocation.NAME_UKNOWN);
            location.setLatitude(GoogleLocation.LAT_UNKNOWN);
            location.setLongitude(GoogleLocation.LON_UNKNOWN);
        }
        return location;
    }

    /**
     *
     * @param geocodeResult - the geocodeResult to be mapped
     * @param location - geocodeResult properties are copied to this param
     * @return
     */
    private GoogleLocation geoCodeResultToAbsLocationAdapter(GeocodeResultDTO geocodeResult, GoogleLocation location) {
        location.setGoogleID(geocodeResult.getResults().get(0).getPlace_id());
        location.setName(geocodeResult.getResults().get(0).getFormatted_address());
        location.setLatitude(geocodeResult.getResults().get(0).getGeometry().getLocation().getLat());
        location.setLongitude(geocodeResult.getResults().get(0).getGeometry().getLocation().getLng());
        return location;
    }

}
