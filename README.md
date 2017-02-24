## Projeto Disparador HTTP

O projeto Disparador HTTP envia mensagens HTTPs para um determinado endpoint.


## Installation

1. Baixe o projeto do repositório Git: [disparador-http](https://github.com/alexlirio/disparador-http.git)
2. Para criar o pacote zip do projeto, execute o seguinte comando maven na pasta raiz do projeto: "mvn clean package".
3. O pacote é criado na pasta do projeto como: "target/disparador-http*.zip". Este arquivo pode ser extraído em qualquer lugar.


## Configuração Necessária

Para o funcionamento é necessário configurar os seguintes arquivos dentro de "cfg/":

1. ** config.properties **, com os valores necessários para o envio da mensagem HTTP. A configuração destas propriedades são opcionais, pois na execução poderemos passar esses valores como uma lista de parâmetros.


## Utilização

Antes de utilizar o disparador, verifique se o serviço que receberá a mensagem HTTP está funcionando.

Existem 4 maneiras de utilização do disparador:

1. Disparar sem passar parâmetros: Neste caso, todas as configurações serão usadas do arquivo "cfg/config.properties".

		DisparadorHTTP disparadorHTTP = new DisparadorHTTP();
		disparadorHTTP.disparar();
		
2. Disparar passando parâmetros: Neste caso, somente as configurações passadas na lista sobrescreverão as usadas do arquivo "cfg/config.properties".

		DisparadorHTTP disparadorHTTP = new DisparadorHTTP();
		Map<String, String> argumentos = new TreeMap<String, String>();
		argumentos.put("http_host", "http://localhost:8080/rebatedor-http/rebatedor-rest-service/post");
		argumentos.put("http_verb", "POST");
		argumentos.put("http_header_1", "Content-Type:application/json");
		disparadorHTTP.disparar(argumentos);
		
3. Disparar utilizando linha de comando sem passar parâmetros: Neste caso, todas as configurações serão usadas do arquivo "cfg/config.properties".

		java -jar nome_do_arquivo.jar
		
4. Disparar utilizando linha de comando passando parâmetros: Neste caso, somente as configurações passadas sobrescreverão as usadas do arquivo "cfg/config.properties".

		java -jar nome_do_arquivo.jar -http_host http://localhost:8080/rebatedor-http/rebatedor-rest-service/post -http_verb POST -http_header_1 Content-Type:application/json}

		
5. Observações sobre Parametros:

- ** http_header_* ** = Para enviar mais de um parâmetro no header, pode se incluir o mesmo numerando. Ex: http\_header\_1, http\_header\_2, etc.

