import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    public static Map<String, Integer> lerConfig(String caminhoArquivo) {
        Map<String, Integer> config = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.contains("=")) {
                    String[] partes = linha.split("=");
                    String chave = partes[0].trim();
                    int valor = Integer.parseInt(partes[1].trim());
                    config.put(chave, valor);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }

        return config;
    }
}
