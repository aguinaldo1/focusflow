# FocusFlow — Documento Mestre de Execução e Decisões

## 1. Propósito

O FocusFlow é um aplicativo desktop de Pomodoro inteligente e adaptativo, criado primeiro para uso real do próprio autor.

O projeto deve demonstrar mais do que capacidade de programar. Ele deve evidenciar:

1. definição de escopo;
2. tomada de decisões técnicas;
3. análise de trade-offs;
4. priorização;
5. capacidade de deixar funcionalidades de fora;
6. validação com uso real;
7. evolução incremental;
8. documentação do raciocínio.

> Regra principal: não construir complexidade por complexidade. Construir apenas o necessário para resolver um problema real e registrar as decisões tomadas.

---

## 2. Problema real

Ao estudar ou executar atividades de concentração, o usuário precisa de um Pomodoro que:

- fique visível sem atrapalhar a tela;
- possa ser acionado rapidamente;
- mantenha até 5 atividades simultâneas;
- permita alternar entre atividades sem perder progresso;
- permita recomeçar ou finalizar uma atividade;
- recomende a quantidade de ciclos conforme a dificuldade;
- mantenha histórico para ajustar futuras recomendações.

O aplicativo será utilizado no dia a dia pelo próprio autor, portanto resolve um problema real fora do repositório.

---

## 3. Hipótese de produto

Se o usuário puder registrar até cinco objetivos, iniciar um cronômetro flutuante e receber uma estimativa simples de esforço, então conseguirá organizar sessões de estudo sem depender de múltiplas ferramentas.

O MVP será considerado útil quando puder ser usado durante uma semana real de estudos.

---

## 4. Escopo do MVP

### Incluído

- Pomodoro de 25 minutos;
- pausa curta de 5 minutos;
- pausa longa após 4 ciclos;
- iniciar, pausar, continuar e reiniciar;
- contagem de ciclos concluídos;
- até 5 objetivos ativos;
- recomeçar objetivo;
- finalizar objetivo;
- adicionar novo objetivo;
- classificação de complexidade 🟢 🟡 🔴;
- estimativa de quantidade de Pomodoros;
- persistência local;
- janela flutuante;
- histórico básico;
- executável Windows.

### Fora do MVP

- login;
- sincronização em nuvem;
- aplicativo mobile;
- colaboração;
- gamificação;
- integração com calendário;
- IA generativa;
- geração automática de questões;
- dashboard avançado;
- backend remoto.

Motivo: nada disso é necessário para validar o problema principal.

---

## 5. Decisões técnicas iniciais

### Java 21

**Motivos:** aprofundar Java, forte tipagem, ecossistema maduro e possibilidade de empacotamento nativo com `jpackage`.

**Alternativas consideradas:** Python, Electron, Tauri e C#/.NET.

**Decisão:** Java 21.

### JavaFX

**Motivos:** integração natural com Java, suporte a janela desktop e `always-on-top`, e evita introduzir uma stack web no MVP.

**Trade-off:** Electron teria ecossistema visual maior, mas acrescentaria JavaScript/Node e maior consumo de recursos.

**Decisão:** JavaFX.

### Maven

**Motivos:** já disponível no ambiente, simples e suficiente para dependências e testes.

**Decisão:** Maven.

### SQLite

**Motivos:** banco local, arquivo único e sem servidor.

**Trade-off:** JSON seria mais simples no início, mas SQLite facilita consultas e evolução futura.

**Decisão:** SQLite somente quando a persistência realmente entrar no projeto.

### Estratégia de desenvolvimento

Separar o motor de Pomodoro da interface gráfica.

Primeiro construir e testar a lógica. Depois construir JavaFX.

**Motivo:** reduzir acoplamento e tornar as regras testáveis sem depender da interface.

---

## 6. Ambiente de desenvolvimento

Ambiente inicial:

- Ubuntu 24.04 em WSL2;
- Java 21;
- Maven;
- Git;
- VS Code.

Estratégia:

- lógica do domínio inicialmente no WSL;
- JavaFX poderá ser validado com WSLg;
- empacotamento `.exe` posteriormente em Windows nativo.

**Motivo:** `jpackage` gera pacote nativo da plataforma em que é executado.

---

## 7. Estratégia Git

Cada commit deve representar um incremento compreensível.

Formato:

```text
tipo: descrição objetiva
```

Tipos preferidos:

- `chore:` estrutura/configuração;
- `docs:` documentação;
- `feat:` funcionalidade;
- `test:` teste;
- `refactor:` melhoria interna;
- `fix:` correção.

Exemplo:

```text
feat: implementa estados básicos do pomodoro
```

---

## 8. Roadmap controlado

### Fase 0 — Fundação

Entregas:

- estrutura inicial;
- README;
- documento de execução;
- `.gitignore`;
- Maven configurado;
- primeiro commit.

Critério de conclusão:

```bash
mvn test
```

executa com sucesso.

### Fase 1 — Motor de Pomodoro

Construir sem GUI.

Estados previstos:

```text
IDLE
FOCUS
SHORT_BREAK
LONG_BREAK
PAUSED
```

Entregas:

- iniciar sessão;
- pausar;
- continuar;
- reiniciar;
- concluir foco;
- concluir pausa;
- contador de Pomodoros.

### Fase 2 — Tempo real

Adicionar contagem regressiva de 25, 5 e pausa longa. O relógio deverá ser testável sem esperar 25 minutos nos testes automatizados.

### Fase 3 — Objetivos

Criar `StudyGoal` com campos iniciais:

```text
id
title
complexity
plannedPomodoros
completedPomodoros
status
```

Regra: máximo de 5 objetivos ativos.

### Fase 4 — Motor de complexidade

Critérios:

- dificuldade conceitual;
- quantidade de operações;
- dificuldade de implementação;
- importância;
- pré-requisitos;
- performance anterior.

Pontuação inicial:

```text
0–4  → 🟢 baixa
5–8  → 🟡 média
9–12 → 🔴 alta
```

### Fase 5 — Persistência

Adicionar SQLite quando as entidades estiverem estáveis.

### Fase 6 — JavaFX

Criar primeira interface funcional, sem preocupação com estética final.

### Fase 7 — Janela flutuante

Adicionar `always-on-top`, modo compacto e arraste.

### Fase 8 — Gestão visual de objetivos

Listar até 5 objetivos, selecionar, recomeçar, finalizar e adicionar novo.

### Fase 9 — Histórico

Registrar sessões concluídas, minutos estudados, Pomodoros realizados e performance.

### Fase 10 — Integração Windows

Ícone, system tray, atalhos e instalador.

### Fase 11 — Uso real

Usar por pelo menos uma semana e registrar:

- o que incomodou;
- o que foi desnecessário;
- o que faltou;
- bugs reais;
- mudanças feitas após uso.

---

## 9. Modelo de registro de decisão

```text
## DEC-XXX — Nome da decisão

Data:
Contexto:
Opções consideradas:
Decisão:
Motivo:
Trade-offs:
Consequências:
```

---

## 10. Modelo de registro de execução

```text
## BLOCO XX — Nome

Objetivo:
Antes de executar:
Comandos executados:
Resultado:
Problemas encontrados:
Decisões tomadas:
Evidência:
Commit:
Próximo passo:
```

---

## 11. Perguntas que o projeto deve responder em entrevista

- Por que esse problema foi escolhido?
- Por que Java 21?
- Por que JavaFX?
- Por que o motor foi separado da interface?
- Por que SQLite em vez de um backend remoto?
- O que foi deliberadamente deixado de fora?
- Quais trade-offs apareceram durante a execução?
- O que mudou depois do uso real?
- Quais bugs reais apareceram?
- Como as decisões foram verificadas?

---

## 12. Métricas de validação

- sessões reais executadas;
- dias em que o app foi usado;
- objetivos concluídos;
- interrupções/bugs;
- diferença entre Pomodoros estimados e realizados;
- funcionalidades removidas após uso real.

---

# 13. Primeira execução — Fase 0

Nesta primeira sessão **não implementar o cronômetro**.

Objetivo:

> Criar uma base reproduzível, documentada e versionada.

## Passo 1 — criar diretório

```bash
mkdir -p ~/projetos/focusflow
cd ~/projetos/focusflow
```

## Passo 2 — iniciar Git

```bash
git init
git branch -M main
```

## Passo 3 — confirmar ambiente

```bash
java -version
mvn -version
git --version
pwd
```

Registrar a saída neste documento.

## Passo 4 — criar estrutura inicial

```bash
mkdir -p docs
mkdir -p src/main/java/io/github/aguinaldo1/focusflow
mkdir -p src/test/java/io/github/aguinaldo1/focusflow

touch README.md
touch docs/EXECUCAO.md
touch .gitignore
touch pom.xml
```

Estrutura esperada:

```text
focusflow/
├── .gitignore
├── README.md
├── pom.xml
├── docs/
│   └── EXECUCAO.md
└── src/
    ├── main/java/io/github/aguinaldo1/focusflow/
    └── test/java/io/github/aguinaldo1/focusflow/
```

## Passo 5 — configurar o Maven mínimo

O `pom.xml` deve começar simples. Dependências de JavaFX, SQLite e outras bibliotecas serão adicionadas somente quando forem necessárias.

Conteúdo inicial:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>io.github.aguinaldo1</groupId>
    <artifactId>focusflow</artifactId>
    <version>0.1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

Conteúdo inicial de `.gitignore`:

```gitignore
target/
.idea/
.vscode/
*.iml
*.log
.DS_Store
```

## Passo 6 — validar a fundação

```bash
mvn test
```

Resultado esperado:

```text
BUILD SUCCESS
```

Esse resultado será a primeira evidência técnica do projeto.

---

## 14. Primeiro checkpoint Git

Somente depois de `mvn test` funcionar:

```bash
git status
git add .
git commit -m "chore: inicia estrutura do projeto FocusFlow"
```

A Fase 0 termina quando:

- a estrutura existe;
- o Maven reconhece o projeto;
- `mvn test` retorna `BUILD SUCCESS`;
- o primeiro commit foi criado.

Somente depois disso começa a Fase 1.

---

## 15. Regra de trabalho daqui em diante

```text
explicar
↓
executar
↓
verificar
↓
documentar
↓
commit
↓
push
↓
próxima etapa
```

O autor executará os comandos em sua máquina e enviará a saída relevante antes de avançarmos.

Isso preserva aprendizado, controle e evidências reais do desenvolvimento.


---

## 16. Diário real de execução

### BLOCO 01 — Validação do ambiente

**Data:** 05/10/2026

**Objetivo**

Confirmar que o ambiente necessário para iniciar o FocusFlow está funcional.

**Evidências coletadas**

#### Java

```text
openjdk 21.0.12.1 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-24.04.4-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-24.04.4-Ubuntu, mixed mode, sharing)
```

#### Maven

```text
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 21.0.12.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.18.33.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

#### Git

```text
git version 2.43.0
```

#### Sistema

```text
Ubuntu 24.04 em WSL2
Kernel: 6.18.33.2-microsoft-standard-wsl2
Arquitetura: amd64
```

#### Diretório do projeto

```text
/home/aguin/projetos/focusflow
```

**Resultado**

✅ Ambiente aprovado para iniciar o desenvolvimento.

- Java 21 está instalado e ativo.
- Maven está executando sobre o Java 21.
- Git está disponível.
- O projeto está em um diretório dedicado dentro do WSL2.
- Não foi identificado bloqueio que justifique atualização de ferramenta antes de começar.

**Decisão**

Manter as versões atuais durante a fundação do projeto. Atualizações de Maven, Java ou Git só serão feitas se surgir uma necessidade concreta durante a implementação.

**Motivo**

Atualizar ferramentas sem necessidade adicionaria mudança e risco sem gerar valor para o MVP.

**Próximo passo**

BLOCO 02 — criar a estrutura Maven mínima, validar o `pom.xml` e executar o primeiro `mvn test`.
