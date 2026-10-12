# 🛡️ Call Shield — Contexto do Projeto e Guia de Retomada

Este documento consolida o estado atual do aplicativo Android **Call Shield**, todas as decisões técnicas tomadas, links para os arquivos salvos e o **prompt/frase mestre** para retomar o desenvolvimento a qualquer momento.

---

## 🚀 Frase / Prompt Mestre para Iniciar ou Retomar esta Conversa

Copie e cole o texto abaixo no início de uma nova conversa no chat para que o assistente carregue instantaneamente todo o contexto:

```text
Continuar desenvolvimento do aplicativo Android Call Shield. O projeto está localizado em "C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp" e no repositório GitHub "lucianogaleski-maker/CallShield". O app gerencia chamadas telefônicas no Android: bloqueia chamadas de voz a nível de chip (SIM) via SimCallScreeningService sem afetar WhatsApp/VoIP, grava chamadas ativas via CallRecorderService acionado por PhoneStateReceiver, e persiste dados e contadores em tempo real via PrefsManager. Leia o arquivo CONTEXTO_E_RETOMADA.md para alinhar o estado atual.
```

---

## 📌 Resumo do Estado Atual do Aplicativo

### 1. Funcionalidades Principais Implementadas
* **Bloqueio a nível de Chip (SIM)**:
  * Utiliza `SimCallScreeningService` herdado de `CallScreeningService` do Android Telecom Framework.
  * Rejeita chamadas celulares de operadora e registra número, data e hora no histórico.
  * **Isolamento de Dados**: Mantém chamadas de WhatsApp, Telegram e Teams funcionando perfeitamente (100% liberadas).
* **Gravação Automática de Ligações**:
  * Utiliza `CallRecorderService` configurado como `ForegroundService` com tipo `microphone`.
  * Acionado automaticamente pelo `PhoneStateReceiver` quando a linha entra em chamada ativa (`OFFHOOK`) e encerrado quando a chamada desliga (`IDLE`).
  * Salva áudios organizados na pasta de mídia do dispositivo (`yyyy-MM-dd_HH-mm-ss_Numero.m4a`).
* **Persistência de Estado e Dados**:
  * Implementado `PrefsManager`: os interruptores (*switches*) não voltam mais para "desligado" sozinhos quando o app é fechado.
  * Histórico das últimas chamadas bloqueadas salvo em armazenamento local.
  * Contadores em tempo real de chamadas bloqueadas e áudios gravados.
* **Proteção contra Suspensão do Android**:
  * Banner e gatilho com 1 toque para desativar a otimização de bateria do sistema (`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`), impedindo que o Android mate os serviços em segundo plano.

---

## 📂 Arquivos Chave do Código-Fonte

* **Activity Principal**: [`MainActivity.kt`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/java/com/exemplo/callutility/ui/MainActivity.kt)
* **Layout Visual**: [`activity_main.xml`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/res/layout/activity_main.xml)
* **Gerenciador de Preferências**: [`PrefsManager.kt`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/java/com/exemplo/callutility/data/PrefsManager.kt)
* **Serviço de Bloqueio SIM**: [`SimCallScreeningService.kt`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/java/com/exemplo/callutility/service/SimCallScreeningService.kt)
* **Serviço de Gravação**: [`CallRecorderService.kt`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/java/com/exemplo/callutility/service/CallRecorderService.kt)
* **Receptor de Telefonia**: [`PhoneStateReceiver.kt`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/java/com/exemplo/callutility/receiver/PhoneStateReceiver.kt)
* **Manifesto Android**: [`AndroidManifest.xml`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/app/src/main/AndroidManifest.xml)

---

## 🌐 Repositório e APKs Compilados

* **GitHub**: [https://github.com/lucianogaleski-maker/CallShield](https://github.com/lucianogaleski-maker/CallShield)
* **Build Mais Recente com Telemetria e Correções**: [Execução #38302992896](https://github.com/lucianogaleski-maker/CallShield/actions/runs/38302992896) (Artifact: `CallShield-app-debug`)
* **Pasta Local no Computador**: [`C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp)
* **Backup Compactado**: [`C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp.zip`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp.zip)

---

## 📜 Históricos Completos das Conversas Salvas

* **Sessão Inicial (Criação do App e CI/CD)**: [`HISTORICO_CHAT.md`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/HISTORICO_CHAT.md)
* **Sessão Atual (Correções, Telemetria e Dashboard)**: [`HISTORICO_CHAT_SESSAO_2.md`](file:///C:/Users/lucia/.gemini/antigravity-ide/scratch/CallShieldApp/HISTORICO_CHAT_SESSAO_2.md)
