# kmind-apm-java

Plugin Java para instrumentação e monitoramento de aplicações, integrando com o **Kmind APM** para rastreamento de requisições, métricas e logs.  
Compatível com aplicações **Java 8+** e frameworks como Spring Boot, Jakarta EE, Micronaut, Quarkus, entre outros.

---

## 📦 Instalação

Este pacote está hospedado no **GitHub Packages**.

### 1. Adicionar autenticação ao GitHub Packages

No arquivo `~/.m2/settings.xml` adicione suas credenciais do GitHub (utilize um [Personal Access Token](https://github.com/settings/tokens) com escopo `read:packages`):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>SEU_USUARIO_GITHUB</username>
      <password>SEU_TOKEN</password>
    </server>
  </servers>
</settings>
