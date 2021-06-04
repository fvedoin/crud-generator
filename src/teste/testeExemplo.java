package teste;

public class testeExemplo {
    public static void main(String[] args) {
        testeDao cs = new testeDao();
        System.out.println("Listando todos os dados da tabela teste");
        for (teste temp : cs.buscateste()) {
            System.out.println("- " + temp.getcodigo() + "- " + temp.getnome());
        }
        teste c1 = new teste(2, "fefis");
        System.out.println("Inserindo dados na tabela teste");
        cs.insereteste(c1);
        System.out.println("Listando todos os dados da tabela teste");
        for (teste temp : cs.buscateste()) {
            System.out.println("- " + temp.getcodigo() + "- " + temp.getnome());
        }
        System.out.println("Alterando o cliente 1");
        cs.editateste(2, "aposts");
        System.out.println("Listando todos os dados da tabela teste");
        for (teste temp : cs.buscateste()) {
            System.out.println("- " + temp.getcodigo() + "- " + temp.getnome());
        }
        System.out.println("Apagando todos os registros da tabela teste");
        for (teste temp : cs.buscateste()) {
            cs.removeteste(temp.getcodigo());
        }
        System.out.println("Listando todos os dados da tabela teste");
        for (teste temp : cs.buscateste()) {
            System.out.println("- " + temp.getcodigo() + "- " + temp.getnome());
        }
    }
}
