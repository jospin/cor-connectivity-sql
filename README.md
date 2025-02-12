# Connectivity Wizard SQL

### O que é este projeto?
**Connectivity Wizard SQL** é uma biblioteca que auxilia com a configuração de um pool de conexões.
<br/><br/>
### Por que eu devo usar isso?
- Independente do número de banco de dados que a sua aplicação use, a configuração é a mesma!.
- Utiliza o poder do [HikariCP](https://github.com/brettwooldridge/HikariCP) para gerenciar o seu Pool de conexões.
- Ao iniciar a sua aplicação, todas as suas conexões estão prontas pra uso.
- Ao iniciar a sua aplicação, você já recebe um diagnóstico de todas as suas bases de dados.
  <br/><br/>
### Como funciona esta biblioteca?
- Quando a sua aplicação sobe, **Connectivity Wizard SQL** é acionado e lê os datasources configurados por você no seu application.yaml ( ou .properties).
- Depois disso, ele instancia cada uma delas e testa a sua conectividade.
  Finalmente, um único JDBC Template é iniciado e armazenado no objeto ConnectivityWizard pra você usar quando necessário.


**Vamos aprender a usá-la...**
<br/><br/>
## Adicione a dependencia no Maven
Adicione no seu **pom.xml** a seguinte dependência:
<br/>
##### pom.xml
```
 <dependency>
 <groupId>br.com.jospin.services.library</groupId>
 <artifactId>cor-connectivity-sql</artifactId>
 <version>1.0.0</version>
 </dependency>
```
<br/><br/>
## Escanear os base packages da aplicação
Na anotação **@SpringBootApplication** da classe principal da sua aplicação adicione a propriedade scanBasePackages com o valor "br.com.jospin.services.library":
<br/>
##### MinhaClassePrincipal.java
```
@SpringBootApplication(scanBasePackages = "br.com.jospin.services.library")
```

Caso sua aplicação utilize um pacote base diferente deste, adicione ele também:
<br/>
##### MinhaClassePrincipal.java
```
@SpringBootApplication(scanBasePackages = {br.com.jospin.services.library", "meu.pacote"})
```
<br/><br/>
## Desabilite a auto-configuração de datasources
Nossa biblioteca vai fazer a configuração dos seus datasources, logo notifique o Spring disso, adicionando a seguinte anotação na classe principal da sua aplicação:
<br/><br/>
#### MinhaClassePrincipal.java
```
@EnableAutoConfiguration(exclude{DataSourceAutoConfiguration.class})
```
<br/><br/>
## Declaração dos drivers dos bancos de dados
Os drivers de MySQL, Progress, SQL Server e PostgreSQL já foram importados pela nossa biblioteca, bem como o Springboot JDBC Starter.
<br/>
##### Dependências já declaradas no pom.xml da biblioteca
```
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-jdbc</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-configuration-processor</artifactId>
 <optional>true</optional>
 </dependency>
 <dependency>
 <groupId>mysql</groupId>
 <artifactId>mysql-connector-java</artifactId>
 <scope>runtime</scope>
 </dependency>
 <dependency>
 <dependency>
 <groupId>com.microsoft.sqlserver</groupId>
 <artifactId>mssql-jdbc</artifactId>
 <scope>runtime</scope>
 </dependency>
 <dependency>
 <groupId>org.postgresql</groupId>
 <artifactId>postgresql</artifactId>
 <scope>runtime</scope>
 </dependency>
```
<br/><br/>
## Declare os seus datasources

No seu arquivo de configurações você precisa informar as configurações gerais do seu pool, como também as configurações referentes a cada conexão do Pool.
Todas as configurações do objeto poolData são opcionais, mas aconselhamos fortemente que sejam informadas. Abaixo um modelo do conteúdo dos arquivos de configurações, tanto com Yaml quanto com Properties.
<br/>
##### Modelo de application.yaml

```
application:
 poolData:
 connectionTimeout: ${POOL_DATA_CONNECTION_TIMEOUT}
 minIdle: ${POOL_DATA_MINIMUM_IDLE}
 maximumPoolSize: ${POOL_DATA_MAXIMUM_POOL_SIZE}
 idleTimeout: ${POOL_DATA_IDLE_TIMEOUT}
 maxLifetime: ${POOL_DATA_MAX_LIFE_TIME}
 autoCommit: ${POOL_DATA_AUTO_COMMIT}
 datasources:
 - name: ${DATASOURCE_SQL_SERVER_NAME}
 driver: ${DATASOURCE_SQL_SERVER_DRIVER}
 url: ${DATASOURCE_SQL_SERVER_URL}
 username: ${DATASOURCE_SQL_SERVER_USERNAME}
 password: ${DATASOURCE_SQL_SERVER_PASSWORD}
```
<br/>

##### Modelo de application.properties
```
application.poolData.connectionTimeout: ${POOL_DATA_CONNECTION_TIMEOUT}
application.poolData.maximumPoolSize: ${POOL_DATA_MAXIMUM_POOL_SIZE}
application.poolData.minimumIdle: ${POOL_DATA_MINIMUM_IDLE}
application.poolData.idleTimeout: ${POOL_DATA_IDLE_TIMEOUT}
application.poolData.maxLifetime: ${POOL_DATA_MAX_LIFE_TIME}
application.poolData.autoCommit: ${POOL_DATA_AUTO_COMMIT}


application.datasources[0].name: ${DATASOURCE_SQL_SERVER_NAME}
application.datasources[0].driver: ${DATASOURCE_SQL_SERVER_DRIVER}
application.datasources[0].url: ${DATASOURCE_SQL_SERVER_URL}
application.datasources[0].username: ${DATASOURCE_SQL_SERVER_USERNAME}
application.datasources[0].password: ${DATASOURCE_SQL_SERVER_PASSWORD}

application.datasources[n].name: ${DATASOURCE_SQL_SERVER_NAME}
application.datasources[n].driver: ${DATASOURCE_SQL_SERVER_DRIVER}
application.datasources[n].url: ${DATASOURCE_SQL_SERVER_URL}
application.datasources[n].username: ${DATASOURCE_SQL_SERVER_USERNAME}
application.datasources[n].password: ${DATASOURCE_SQL_SERVER_PASSWORD}
```
<br/><br/>
## Obtendo o status de cada Datasource
Chame o método connectionsHealth() de ConnectionWizard de forma estática.
Este método vai te retornar um Map<String, Boolean> listando o resultado do teste de conexão que foi feito assim que a sua aplicação subiu.
```
Map<String, Boolean> = ConnectionWizard.connectionsHealth();
```

<br/>

##### Conteúdo do Map:
| Nome do datasource | Saudável? |
|--------------------|-----------|
| teste1             | true |
| teste2             | true |
| teste3             | false |

Observe que houve um problema na inicialização do datasource 'teste3' não foi inicializado corretamente;
Podemos observar o motivo nos logs da aplicação:

```
2020-03-06 19:13:18.108 ERROR 8756 --- [main] com.zaxxer.hikari.pool.HikariPool: teste3 - Exception during pool initialization.
java.sql.SQLException: [DataDirect][OpenEdge JDBC Driver]Software caused connection abort: recv failed.
```
<br/><br/>
## Obtendo um JDBCTemplate de cada Datasource
Chame o método templates() de ConnectionWizard de forma estática.
Este método vai te retornar um Map<String, JdbcTemplate>.
JdbcTemplate já foi iniciado quando a aplicação subiu e está pronto para uso.
Caso tenha ocorrido algum problema na inicialização de algum datasource, o Map não terá nenhuma entrada com o respectivo seu nome.


```
/*jdbcTemplate será nulo caso tenha ocorrido algum erro na inicializacao do datasource,
ou se você informou o nome errado*/

JdbcTemplate jdbcTemplate = ConnectionWizard.templates().get(jdbcTemplateName);
```
<br/><br/>
## Dicionário de dados dos arquivos de configuração
- **application.pooldata.connectionTimeout**:
> Essa propriedade controla o número máximo de milissegundos que um cliente (que é você) aguardará uma conexão do pool. Se esse tempo for excedido sem que uma conexão se torne disponível, será lançada uma SQLException. O tempo limite de conexão aceitável mais baixo é de 250 ms. Padrão: 30000 (30 segundos)
>
- **application.pooldata.maximumPoolSize**:
>Essa propriedade controla o tamanho máximo que o pool pode atingir, incluindo conexões inativas e em uso. Basicamente, esse valor determinará o número máximo de conexões reais com o back-end do banco de dados. Um valor razoável para isso é melhor determinado pelo seu ambiente de execução. Quando o pool atingir esse tamanho e nenhuma conexão inativa estiver disponível, as chamadas para getConnection () serão bloqueadas por até connectionTimeout milissegundos antes do tempo limite. Por favor, leia sobre o dimensionamento da piscina. Padrão: 10
>
- **application.pooldata.minimumIdle**:
>Esta propriedade controla o número mínimo de conexões inativas que o HikariCP tenta manter no pool. Se as conexões inativas estiverem abaixo desse valor e o total de conexões no pool for menor que maximumPoolSize, o HikariCP fará o possível para adicionar conexões adicionais de maneira rápida e eficiente. No entanto, para obter o máximo desempenho e capacidade de resposta às demandas, recomendamos não definir esse valor e permitir que o HikariCP atue como um conjunto de conexões de tamanho fixo. Padrão: igual a maximumPoolSize
>
- **application.pooldata.idleTimeout**:
>Essa propriedade controla a quantidade máxima de tempo que uma conexão pode ficar ociosa no pool. Essa configuração se aplica somente quando minimumIdle é definido como menor que maximumPoolSize. As conexões inativas não serão desativadas quando o pool atingir as conexões minimumIdle. O fato de uma conexão ser desativada ou não está sujeita a uma variação máxima de +30 segundos e uma variação média de +15 segundos. Uma conexão nunca será retirada como inativa antes desse tempo limite. Um valor 0 significa que as conexões inativas nunca são removidas do pool. O valor mínimo permitido é 10000ms (10 segundos). Padrão: 600000 (10 minutos)
>
- **application.pooldata.maxLifetime**:
>Esta propriedade controla a vida útil máxima de uma conexão no pool. Uma conexão em uso nunca será desativada, somente quando estiver fechada será removida. Em uma conexão por conexão, uma atenuação negativa menor é aplicada para evitar a extinção em massa no pool. É altamente recomendável definir esse valor e deve ser vários segundos mais curto que qualquer limite de tempo de conexão imposto por banco de dados ou infraestrutura. Um valor 0 indica que não há vida útil máxima (vida útil infinita), sujeita, é claro, à configuração idleTimeout. Padrão: 1800000 (30 minutos)
>
- **application.pooldata.autoCommit**:
>Esta propriedade controla o comportamento de confirmação automática padrão das conexões retornadas do pool. É um valor booleano. Padrão: true
>
- **application.datasources[0..n].name**:
>Nome do datasource. Você irá recuperar tanto um JDBCTemplate como a saúde da sua conexão a partir desse nome. Veja a seção referente ao objeto **ConnectivityWizard** para entender melhor como utilizar.
>
- **application.datasources[0..n].driver**:
>O HikariCP tentará resolver um driver através do DriverManager baseado apenas no jdbcUrl, mas para alguns drivers mais antigos, o identificador do driver também deve ser especificado. Vamos informá-lo como padrão.
>
- **application.datasources[0..n].url**:
>A URL da seu datasource no formato:
>**jdbc//[host][/database][?properties]**
>
- **application.datasources[0..n].username**:
>Essa propriedade define o nome de usuário de autenticação padrão do seu datasource.
>
- **application.datasources[0..n].password**:
>Esta propriedade define a senha de autenticação padrão do seu datasource.

<br/><br/>

### Release Notes
| Versão | Data | Descrição |
| :---: | :---: | :--- |
| 1.0.0 | 18/03/2020 | Versão inicial |