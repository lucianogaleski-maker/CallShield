# Histórico de Desenvolvimento e Revisões Técnicas - Call Shield

Este documento registra o escopo técnico, decisões arquiteturais e resolução de problemas ocorridos durante o ciclo de desenvolvimento do aplicativo.

---

## 📋 Escopo e Requisitos Atendidos

1. **Gravação de Áudio com Formatos e Nomenclatura**:
   - Implementado serviço `CallRecorderService` com tipagem `microphone` para atender aos requisitos de segurança do Android 14+.
   - Sistema de nomes padronizado: `ANO-MES-DIA_HORA_NumeroDoTelefone.extensao`.
   - Compatibilidade com formatos WAV, OGG e MP3/M4A.

2. **Bloqueio SIM com Whitelist de WhatsApp**:
   - Utilização da API oficial `CallScreeningService`.
   - O tráfego VoIP (WhatsApp, Telegram) não é interceptado por esta API, garantindo nativamente a continuidade das chamadas de aplicativos de mensagens.

3. **Ícone Inicializável (*Adaptive Icon*)**:
   - Criação dos recursos vetoriais `ic_launcher_background.xml` e `ic_launcher_foreground.xml` (escudo ciano com receptor telefônico branco em fundo dark).
   - Definição em `res/mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml`.
   - Asset visual em alta resolução gerado e salvo como `app_icon.png`.

4. **Desinstalação Fácil**:
   - Botão dedicado na interface que dispara `Intent(Intent.ACTION_UNINSTALL_PACKAGE)`, permitindo ao usuário remover o app com um toque e restaurar as configurações padrão de chamada do sistema.

---

## 🛠️ Resolução de Erros Durante a Compilação (Troubleshooting)

### 1. Erro de Dependências no Gradle (Build Failed in 8s):
- **Causa**: Ausência do arquivo `gradle.properties` com `android.useAndroidX=true`, gerando inconsistência nas dependências de bibliotecas de suporte legado vs. AndroidX moderna (`com.google.android.material:material:1.11.0`).
- **Solução**: Criação do arquivo `gradle.properties` configurando memória JVM (`-Xmx2048m`) e flags `android.useAndroidX=true` e `android.nonTransitiveRClass=true`.

### 2. Incompatibilidade de Versão do Gradle Wrapper:
- **Causa**: Wrapper inicial continha versão inexistente/instável e o comando `gradle wrapper` na esteira sobrescrevia a versão recomendada.
- **Solução**: Travada a versão no arquivo `gradle/wrapper/gradle-wrapper.properties` para **Gradle 8.5** e ajustado o workflow para executar `./gradlew assembleDebug --stacktrace` diretamente.

### 3. Ajuste de Tema no Manifesto:
- **Causa**: Referência indireta de tema genérico.
- **Solução**: Vinculado diretamente ao `@style/Theme.CallShield` criado em `themes.xml`.

---

## 🔗 Referências e Repositório

- **Repositório GitHub**: [https://github.com/lucianogaleski-maker/CallShield](https://github.com/lucianogaleski-maker/CallShield)
- **Execução do Build Bem-sucedida**: [Workflow Run #38287773955](https://github.com/lucianogaleski-maker/CallShield/actions/runs/38287773955)
- **Localização dos Arquivos Locais**: `C:\Users\lucia\.gemini\antigravity-ide\scratch\CallShieldApp`
