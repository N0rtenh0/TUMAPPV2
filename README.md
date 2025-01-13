# Relatório do Projeto Kotlin TUMAPPV2

## Descrição Geral
O projeto **TUMAPPV2** é um aplicativo Android desenvolvido em **Kotlin**, que utiliza **Jetpack Compose** para a interface de usuário e **Firebase** para autenticação e armazenamento de dados. Ele permite que os usuários realizem login, cadastro, personalizem seus perfis e interajam com eventos registrados no sistema.

---

## Funcionalidades Principais

### 1. **Autenticação com Firebase**
- **Login com Email/Senha**:
  - Tela de login que valida as entradas de email e senha.
  - Integração com Firebase Authentication para autenticação segura.
- **Registro de Novos Usuários**:
  - Formulário de cadastro com validações de email e senha.
  - Geração de nomes de usuário aleatórios e salvamento no Firestore.
- **Login com Google**:
  - Integração com a API do Google para login.
  - Suporte para autenticação OAuth2 com Firebase.

### 2. **Gestão de Perfil**
- **Edição de Dados do Usuário**:
  - Permite aos usuários atualizar o nome de usuário diretamente no Firestore.
  - Verificação do método de autenticação (Email/Google).
- **Logout e Exclusão de Conta**:
  - Funcionalidades para encerrar sessão.
  - Opção de excluir conta, com reautenticação obrigatória para segurança.

### 3. **Gestão de Eventos**
- **Listagem de Eventos**:
  - Exibição de eventos armazenados no Firestore.
  - Listagem de participantes para cada evento.
- **Criação de Eventos**:
  - Permite adicionar novos eventos ao Firestore, com campos como data, local e tipo.
- **Confirmação de Presença**:
  - Os usuários podem confirmar ou cancelar presença em eventos.
  - Atualização automática da lista de participantes.

### 4. **Navegação com Jetpack Navigation**
- Navegação declarativa usando Jetpack Navigation Compose.
- Rotas definidas na classe `Destino` para modularidade:
  - **Login** (`EcraLoginFirebase`)
  - **Registro** (`EcraRegisterToFirebase`)
  - **Configurações** (`EcraSettings`)
  - **Lista de Eventos** (`Ecra03`)
  - **Edição de Eventos** (`Ecra04`)

---

## Estrutura do Código

### **Pacotes e Arquivos**
1. **`MainActivity`**:
   - Configuração inicial do tema e navegação.
2. **`EcraLoginFirebase`**:
   - Tela de login com email/senha e Google.
3. **`EcraRegisterToFirebase`**:
   - Tela de cadastro de novos usuários.
4. **`EcraSettings`**:
   - Tela de configurações do usuário, incluindo edição de perfil e exclusão de conta.
5. **`Ecra03`**:
   - Tela para exibição e gerenciamento de eventos.
6. **`Ecra04`**:
   - Tela para criação de novos eventos.
7. **`Destinos.kt`**:
   - Definição das rotas de navegação.

### **Integrações com Firebase**
- **Firestore**:
  - Armazena dados de usuários e eventos.
  - Usa coleções para "users", "atuacoes" (eventos) e "reactions" (presenças).
- **Authentication**:
  - Garante acesso seguro usando email/senha e OAuth2 (Google).

---

## Recursos Adicionais

- **Validação de Dados**:
  - Validação de email com `android.util.Patterns`.
  - Verificação de senha com comprimento mínimo de 6 caracteres.
- **UI Responsiva**:
  - Uso de `Modifier` do Jetpack Compose para layouts flexíveis.
- **Mensagens e Logs**:
  - Feedback visual ao usuário com `Toast`.
  - Logs para depuração usando `Log.d`.

---

## Melhorias Futuras

1. **Internacionalização Completa**:
   - Suporte para múltiplos idiomas nos textos exibidos.
2. **Segurança Avançada**:
   - Implementar autenticação em duas etapas (2FA).
3. **Notificações**:
   - Notificar os participantes sobre alterações em eventos.
4. **Design Refinado**:
   - Adicionar animações e melhorias de UI.
5. **Suporte Offline**:
   - Sincronização local dos eventos para uso offline.

---

## Conclusão

O projeto TUMAPPV2 é uma solução robusta para gestão de eventos e interações sociais, aproveitando o poder do Firebase e Jetpack Compose. Ele pode ser ampliado com funcionalidades adicionais para atender a uma base de usuários ainda maior.

