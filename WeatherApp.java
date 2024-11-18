import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherApp {
    private static final String API_KEY = ""; // Ваш API ключ

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод широты
        System.out.print("Введите широту: ");
        double latitude = scanner.nextDouble();

        // Ввод долготы
        System.out.print("Введите долготу: ");
        double longitude = scanner.nextDouble();

        // Ввод лимита
        System.out.print("Введите количество дней для прогноза (лимит): ");
        int limit = scanner.nextInt();

        try {
            // Формируем URL для запроса
            String urlString = String.format("https://api.weather.yandex.ru/v2/forecast?lat=" + latitude + "&lon=" + longitude + "&limit=" + limit);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-API-Key", API_KEY);

            // Проверяем статус ответа
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем ответ
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Обрабатываем JSON-ответ
                JSONObject jsonResponse = new JSONObject(response.toString());
                System.out.println("Все данные от сервиса:");
                System.out.println(jsonResponse.toString(2)); // Форматируем вывод

                // Извлекаем текущую температуру
                int currentTemp = jsonResponse.getJSONObject("fact").getInt("temp");
                System.out.println("\nТекущая температура: " + currentTemp + "°C");

                // Вычисляем среднюю температуру за период
                JSONArray forecasts = jsonResponse.getJSONArray("forecasts");
                double totalTemp = 0;
                int count = 0;

                for (int i = 0; i < forecasts.length(); i++) {
                    JSONObject forecast = forecasts.getJSONObject(i);
                    totalTemp += forecast.getJSONObject("parts").getJSONObject("day").getDouble("temp_avg");
                    count++;
                }

                double averageTemp = totalTemp / count;
                System.out.println("\nСредняя температура за " + count + " дней: " + averageTemp + "°C");
            } else {
                System.out.println("Ошибка при запросе: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close(); // Закрываем сканер
        }
    }
}
