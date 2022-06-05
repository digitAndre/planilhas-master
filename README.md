# Planilhas
Biblioteca utilitária em Java que faz a leitura de planilhas XLSX, XLS e CSV e converte as linhas para Objetos.

Foi baseada em outra lib excelr, porém com algumas modificações úteis nas regras de leitura.

Exemplo de uso:

    RowConverter<Country> conversor = (row) -> new Country((String) row[0], (String) row[1]);

    LeitorPlanilha<Country> leitor = LeitorPlanilha.builder(Country.class)
            .converter(conversor)
            .delimitadorCsv(';')
            .aba("nome_da_aba")
            .comCabecalho()
            .build();
    
    List<Country> lista;
    lista = leitor.ler("src/test/resources/CountryCodes.xlsx");
    lista = leitor.ler("src/test/resources/CountryCodes.xls");
    lista = leitor.ler("src/test/resources/CountryCodes.csv");

Arquivo da planilha:

	Code	Country
	ad	Andorra
	ae	United Arab Emirates
	af	Afghanistan
	ag	Antigua and Barbuda
	...

Classe Country:

    public static class Country {
        public String shortCode;
        public String name;
    
        public Country(String shortCode, String name) {
            this.shortCode = shortCode;
            this.name = name;
        }
    }
