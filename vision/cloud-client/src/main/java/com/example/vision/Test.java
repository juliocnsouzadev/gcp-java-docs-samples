/*
 * Copyright 2017 julio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.vision;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 *
 * @author julio
 */
public class Test {

    public static void main( String[] args )
            throws Exception {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(
                new FileInputStream( "test.pdf" ) );

        Image img = Image.newBuilder().setContent( imgBytes ).build();
        Feature feat = Feature.newBuilder().setType( Type.TEXT_DETECTION ).build();
        AnnotateImageRequest request
                             = AnnotateImageRequest.newBuilder().addFeatures(
                        feat ).setImage( img ).build();
        requests.add( request );

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
                ServiceAccountCredentials.fromStream( new FileInputStream(
                        "credentials.json" ) ) );
        ImageAnnotatorSettings.defaultBuilder().setCredentialsProvider(
                credentialsProvider );

        try ( ImageAnnotatorClient client = ImageAnnotatorClient.create() ) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(
                    requests );
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for ( AnnotateImageResponse res : responses ) {
                if ( res.hasError() ) {
                    out.printf( "Error: %s\n" , res.getError().getMessage() );
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for ( EntityAnnotation annotation : res.getTextAnnotationsList() ) {
                    out.printf( "Text: %s\n" , annotation.getDescription() );
                    out.printf( "Position : %s\n" , annotation.getBoundingPoly() );
                }
            }
        }
    }

}
