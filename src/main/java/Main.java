import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet
                ("https://api.nasa.gov/planetary/apod?api_key=aIgou3he9wpbke51SHua44jGveywgauVrFIZE03W");

        CloseableHttpResponse response = httpClient.execute(request);

        ApiNasaData data = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        System.out.println(data);

        //скачивание картинки
        String hdurl = data.getHdurl();

        HttpGet requestImage = new HttpGet(hdurl);

        CloseableHttpResponse responseImage = httpClient.execute(requestImage);

        //разложение ссылки на части с целью получить название картинки
        String[] hdurlDecomposition = hdurl.split("/");

        //запись картинки в файл
        try (FileOutputStream fos = new FileOutputStream
                (hdurlDecomposition[hdurlDecomposition.length-1])) {
            byte[] image = responseImage.getEntity().getContent().readAllBytes();
            fos.write(image, 0, image.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
