# Planilhas
Utilidade básica em Java que faz a leitura de planilhas XLSX, XLS e CSV e converte as linhas para Objetos em Java.

Foi baseada em outra lib excelr, porém com algumas modificações úteis nas regras de leitura.

Examplo de uso:

		RowConverter<Country> converter = (row) -> new Country((String) row[0], (String) row[1]);

		LeitorPlanilha<Country> reader = LeitorPlanilha.builder(Country.class)
				.converter(converter)
				.delimitadorCsv(';')
				.aba("Sheet1")
				.comCabecalho()
				.build();
		
		List<Country> list;
		list = reader.ler("src/test/resources/CountryCodes.xlsx");
		list = reader.ler("src/test/resources/CountryCodes.xls");
		list = reader.ler("src/test/resources/CountryCodes.csv");
