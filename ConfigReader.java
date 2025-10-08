import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    public static Map<String, String> lerConfig(String caminhoArquivo) {
        Map<String, String> config = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.contains("=") && !linha.trim().startsWith("#")) {
                    String[] partes = linha.split("=");
                    if (partes.length >= 2) {
                        String chave = partes[0].trim();
                        String valor = partes[1].trim();
                        config.put(chave, valor);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
        return config;
    }

    public static int getInt(Map<String, String> cfg, String key) {
        return Integer.parseInt(cfg.get(key));
    }

    public static double getDouble(Map<String, String> cfg, String key) {
        return Double.parseDouble(cfg.get(key));
    }
}
