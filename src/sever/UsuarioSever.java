
package sever;

import biblioteca.Administrador;
import biblioteca.Bibliotecario;
import biblioteca.Usuario;
import java.util.ArrayList;
import java.util.Scanner;
import dados.Dados.*;
import verficacoes.verficacoes.*;
import biblioteca.Biblioteca;
public class UsuarioSever {
     static Scanner scanner = new Scanner(System.in);
    public static boolean login() {
        if (dados.Dados.getUsuarios().isEmpty() && dados.Dados.getBibliotecarios().isEmpty() && dados.Dados.getAdministradores().isEmpty()) {
            System.out.println("Nenhum usuário cadastrado. Por favor, crie uma conta primeiro.");
            criarConta();
            return true; // Login automático após criar conta
        }
        System.out.println("Bem-vindo!");
        System.out.println("1. Login");
        System.out.println("2. Criar Conta");
        System.out.println("3. Encerrar Sistema");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        if (escolha == 3) {
            System.out.println("Sistema encerrado. Até logo!");
            System.exit(0); // Encerra o sistema
        }

        if (escolha == 2) {
            criarConta();
            return true; // Login automático após criar conta
        }

        System.out.println("Nível de conta: 1 para usuário, 2 para bibliotecário, 3 para adm");
        int nivelConta = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer
        String pesEmail;

        switch (nivelConta) {
            case 1:
                pesEmail = solicitarEmail(dados.Dados.getUsuarios());
                if (pesEmail == null) return false; // Se não encontrar o e-mail, retorna
                return autenticarUsuario(dados.Dados.getUsuarios(), pesEmail, "usuario");

            case 2:
                pesEmail = solicitarEmail(dados.Dados.getBibliotecarios());
                if (pesEmail == null) return false; // Se não encontrar o e-mail, retorna
                return autenticarUsuario(dados.Dados.getBibliotecarios(), pesEmail, "bibliotecario");

            case 3:
                pesEmail = solicitarEmail(dados.Dados.getAdministradores());
                if (pesEmail == null) return false; // Se não encontrar o e-mail, retorna
                return autenticarUsuario(dados.Dados.getAdministradores(), pesEmail, "adm");

            default:
                System.out.println("Opção inválida.");
                return false;
        }
    }
    public static void criarConta() {
        System.out.println("Bem-vindo! Primeiro, nos diga o nível da conta: 1 para usuário, 2 para bibliotecário, 3 para adm.");
        int escolhaConta = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        System.out.println("CPF:");
        String testeCpf = scanner.nextLine();

        if (!verficacoes.verficacoes.validarCPF(testeCpf)) {
            System.out.println("CPF inválido. Conta não criada.");
            return;
        }

        System.out.println("Nome:");
        String nome = scanner.nextLine();

        System.out.println("Email:");
        String email = scanner.nextLine();

        System.out.println("Senha:");
        String senha = scanner.nextLine();

        System.out.println("Confirme a sua senha:");
        String confirmaSenha = scanner.nextLine();

        if (!senha.equals(confirmaSenha)) {
            System.out.println("As senhas não coincidem. Conta não criada.");
            return;
        }

        switch (escolhaConta) {
            case 1: // Usuário
                if (verficacoes.verficacoes.verificaCpfExistente(dados.Dados.getUsuarios(), testeCpf)) return;
                Usuario u = new Usuario(dados.Dados.getContadorUsuario(), testeCpf, nome, email, senha);
                dados.Dados.getUsuarios().add(u);
                System.out.println("Conta de usuário criada com sucesso.");
                dados.Dados.setTipoUsuario("usuario");
                dados.Dados.setPosicaoUsuario(dados.Dados.getUsuarios().size() - 1) ;
                dados.Dados.AumentarContardoUsuario();
                break;

            case 2: // Bibliotecário
                if (verficacoes.verficacoes.verificaCpfExistente(dados.Dados.getBibliotecarios(), testeCpf)) return;
                Bibliotecario b = new Bibliotecario("bli" + (dados.Dados.getContadorUsuario()), dados.Dados.getContadorUsuario(), testeCpf, nome, email, senha);
                dados.Dados.AumentarContardoUsuario();
                dados.Dados.getBibliotecarios().add(b);
                System.out.println("Conta de bibliotecário criada com sucesso.");
                dados.Dados.setTipoUsuario("bibliotecario");
                dados.Dados.setPosicaoUsuario(dados.Dados.getBibliotecarios().size() - 1); // Loga automaticamente
                dados.Dados.AumentarContardoUsuario();
                break;

            case 3: // Administrador
                if (verficacoes.verficacoes.verificaCpfExistente(dados.Dados.getAdministradores(), testeCpf)) return;
                Administrador a = new Administrador("ADM" + (dados.Dados.getContadorUsuario()), dados.Dados.getContadorUsuario(), testeCpf, nome, email, senha);
                dados.Dados.getAdministradores().add(a);
                System.out.println("Conta de administrador criada com sucesso.");
                dados.Dados.setTipoUsuario("adm");
                dados.Dados.setPosicaoUsuario( dados.Dados.getAdministradores().size() - 1); // Loga automaticamente
                break;

            default:
                System.out.println("Opção inválida.");
                break;
        }
    }
     private static String solicitarEmail(ArrayList<? extends Usuario> lista) {
        System.out.println("Email:");
        String pesEmail = scanner.nextLine();

        // Verifica se o e-mail está cadastrado
        for (Usuario usuario : lista) {
            if (usuario.getEmail().equalsIgnoreCase(pesEmail)) {
                return pesEmail; // Retorna o e-mail se encontrado
            }
        }
        System.out.println("E-mail não cadastrado. Tente novamente.");
        return null; // Retorna null se o e-mail não for encontrado
    }

    private static boolean autenticarUsuario(ArrayList<? extends Usuario> lista, String email, String tipo) {
        String pesSenha;
        Usuario usuarioEncontrado = null;

        // Busca o usuário pelo e-mail
        for (Usuario usuario : lista) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                usuarioEncontrado = usuario;
                break;
            }
        }

        if (usuarioEncontrado == null) {
            System.out.println("E-mail não cadastrado.");
            return false; // E-mail não encontrado
        }

        // Autentica o usuário
        int tentativas = 0; // Contador de tentativas
        while (tentativas < 3) {
            System.out.println("Senha:");
            pesSenha = scanner.nextLine();
            if (usuarioEncontrado.getSenha().equals(pesSenha)) {
                dados.Dados.setPosicaoUsuario(usuarioEncontrado.getIdusuario());
                dados.Dados.setTipoUsuario(tipo);
                System.out.println("Login feito com sucesso, " + usuarioEncontrado.getNome() + "!");
                if (tipo.equals("usuario")) {
                   
                } else if (tipo.equals("bibliotecario")) {
                    
                } else {
                  
                }
                return true;
            } else {
                tentativas++;
                System.out.println("Senha incorreta. Tentativa " + tentativas + " de 3.");
            }
        }

        // Se o usuário errar a senha 3 vezes
        System.out.println("Você excedeu o número de tentativas. Deseja criar uma nova conta? (s/n)");
        String resposta = scanner.nextLine();
        if (resposta.equalsIgnoreCase("s")) {
            criarConta();
        }
        return false; // Retorna falso após 3 tentativas
    } 
   public static void visualizarHistoricoEmprestimos(int idUsuario) {
        if (idUsuario < 0 || idUsuario >= dados.Dados.getUsuarios().size()) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        dados.Dados.getUsuarios().get(idUsuario).visualizarHistorico();
    }
    static void loginAutomatico(int posicao, String tipousuario){
      dados.Dados.setPosicaoUsuario(posicao);
      dados.Dados.setTipoUsuario(tipousuario);
    }
}
