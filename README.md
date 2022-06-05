# Planilhas
Utilidade básica em Java que faz a leitura de planilhas XLSX, XLS e CSV e converte as linhas para Objetos em Java.

Foi baseada em outra lib excelr, porém com algumas modificações úteis nas regras de leitura.

Examplo de uso:

		RowConverter<Country> conversor = (row) -> new Country((String) row[0], (String) row[1]);

		LeitorPlanilha<Country> leitor = LeitorPlanilha.builder(Country.class)
				.converter(conversor)
				.delimitadorCsv(';')
				.aba("Sheet1")
				.comCabecalho()
				.build();
		
		List<Country> lista;
		lista = leitor.ler("src/test/resources/CountryCodes.xlsx");
		lista = leitor.ler("src/test/resources/CountryCodes.xls");
		lista = leitor.ler("src/test/resources/CountryCodes.csv");
