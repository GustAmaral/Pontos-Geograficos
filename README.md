# Prática 03 — Programação para Dispositivos Móveis

Aplicativo Android desenvolvido para a disciplina **INF311 - Programação para Dispositivos Móveis** da Universidade Federal de Viçosa (UFV).

## Descrição

O app exibe pontos geográficos importantes relacionados ao programador, utilizando o Google Maps SDK. O usuário pode navegar entre localizações, visualizar sua posição atual e consultar um histórico de acessos.

## Funcionalidades

- **Atividade 1** — Menu principal com três localizações geográficas (cidade natal, casa em Viçosa e departamento). Ao selecionar uma opção, o mapa é centralizado no local correspondente com um marcador.
- **Atividade 2** — Botão de localização atual que adiciona um marcador azul na posição do usuário e exibe a distância em linha reta até a casa em Viçosa.
- **Atividade 3** — Banco de dados SQLite para persistência das localizações e logs de acesso, com tela de relatório listando o histórico e exibindo coordenadas via INNER JOIN.

## Tecnologias utilizadas

- Java
- Android Studio
- Google Maps SDK for Android
- Google Play Services Location
- SQLite

## Como executar

1. Clone o repositório:
   ```bash
   git clone git@github.com:SEU_USUARIO/SEU_REPOSITORIO.git
   ```

2. Abra o projeto no Android Studio.

3. Crie um arquivo `local.properties` na raiz do projeto e adicione sua chave da API do Google Maps:
   ```
   MAPS_API_KEY=sua_chave_aqui
   ```

4. Sincronize o Gradle e execute o app em um emulador ou dispositivo físico.

## Observação

O arquivo `local.properties` **não está incluso no repositório** por conter a chave de API. É necessário gerá-la no [Google Cloud Console](https://console.cloud.google.com) com o **Maps SDK for Android** habilitado.

## Autor

Gustavo Henrique Amaral Barbosa — Universidade Federal de Viçosa