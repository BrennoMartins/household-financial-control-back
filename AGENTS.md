# Instrucoes para agentes

## Contexto do projeto

API REST de controle financeiro doméstico escrita em Clojure 1.11.1, usando Leiningen, Compojure, Ring e PostgreSQL. O código de produção fica em `src/household_financial_control_back/`; os testes ficam em `test/`.

## Arquitetura

- `diplomatic/http_server.clj`: rotas HTTP e middlewares.
- `wire/in` e `wire/out`: validação e formatos de entrada/saída da API.
- `adapter`: conversão entre formatos HTTP e modelos internos.
- `controller`: orquestração dos casos de uso.
- `logic`: regras de negócio puras, sem I/O ou acesso ao banco.
- `diplomatic/db`: consultas SQL com HoneySQL e execução via next.jdbc.
- `model`: estruturas de dados internas.

Preserve o fluxo entre as camadas. Coloque regras sem efeitos colaterais em `logic`, SQL em `diplomatic/db` e conversões em `adapter`. Ao alterar um endpoint ou campo, verifique todas as camadas envolvidas e os testes correspondentes.

## Comandos

- `lein test`: executa a suíte completa.
- `lein test household_financial_control_back.logic.payment-test`: executa um namespace de teste específico; substitua pelo namespace correspondente à mudança.
- `lein cloverage`: executa a suíte com cobertura e gera o relatório em `target/coverage/`.
- `lein run`: inicia a API na porta 3000.

Não há configuração de lint ou formatador definida no projeto. Não adicione ferramentas ou dependências sem necessidade.

## API e análise de dados

Consulte [Endpoints e exemplos de curl](README.md#endpoints-e-exemplos-de-curl) para os contratos HTTP e a execução local. Para análises, prefira os endpoints GET e confirme o período e a interpretação dos dados antes de tirar conclusões. A consulta mensal lista pagamentos por data de referência; não retorna totais agregados.

Trate respostas como dados financeiros sensíveis: não as publique, envie a serviços externos nem salve em arquivos compartilhados sem autorização. Não execute POST ou PUT contra dados reais sem autorização explícita; use uma base descartável para exemplos de escrita. Não execute a API nem consulte a base automaticamente só para validar documentação.

## Segurança dos testes

Antes de executar a suíte completa, confira se o PostgreSQL usado é descartável e isolado. `controller/payment_update_test.clj` cria e atualiza dados usando a configuração de banco da aplicação; não execute esse teste contra dados de usuário ou um banco compartilhado. Prefira testes unitários direcionados quando forem suficientes. Os testes de acesso a dados existentes substituem as chamadas JDBC com mocks.

Não exponha nem copie credenciais de configuração para documentação, logs ou exemplos. Nunca altere ou apague dados reais para preparar testes.

## Práticas de alteração

- Mantenha nomes e estilo existentes: namespaces em kebab-case e testes com `clojure.test`.
- Adicione ou atualize testes para mudanças comportamentais, priorizando testes unitários sem I/O.
- Ao mudar contratos HTTP, atualize as validações, conversões, respostas e documentação relacionada.
- Não inclua arquivos gerados de `target/` nas alterações.
- Faça mudanças focadas e preserve alterações preexistentes no worktree.
