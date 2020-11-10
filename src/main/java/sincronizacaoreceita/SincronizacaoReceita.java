/*
Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do Sicredi recebe e organiza as informações de contas para enviar ao Banco Central. Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o Sicredi já possiu mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, antes as 10:00 da manhã na abertura das agências.

Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma nova coluna.


Formato CSV:
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
...

*/
package sincronizacaoreceita;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SincronizacaoReceita {

    public static void main(String[] args) {
    	
    	SpringApplication.run(SincronizacaoReceita.class, args);
        
//    	String str = "Hello";
//    	String path = System.getProperty("user.dir");
        BufferedWriter writer;
        StringBuilder sbOut = new StringBuilder();
		try {
			File file = new File(args[0]);
			String basePath = file.getParent();
			String fileName = file.getName().replace(".csv", "");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			ReceitaService receitaService = new ReceitaService();
			Boolean cabecalho = true;
			while((line = bufferedReader.readLine()) != null){
				// Ignorar cabe�alho
				if(!cabecalho){
					String[] dados = line.split(";");
					try{
						Boolean result = receitaService.atualizarConta(dados[0], dados[1], Double.valueOf(dados[2].replace(",",".")), dados[3]);
						sbOut.append(line + ";" + (result?"OK":"ERRO"));
						
					}catch (RuntimeException e) {
						sbOut.append(line + ";"+"ERRO");
					}
				}else{
					sbOut.append(line + ";retorno");
				}
				
				sbOut.append("\n");
				cabecalho = false;
			}
			// Exemplo como chamar o "serviço" do Banco Central.
//	        ReceitaService receitaService = new ReceitaService();
//	        receitaService.atualizarConta("0101", "123456", 100.50, "A");
	        
			writer = new BufferedWriter(new FileWriter(basePath+"/"+fileName+"_retorno.csv"));
			writer.write(sbOut.toString());
	        
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
}
