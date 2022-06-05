# Planilhas
Utilidade básica em Java que faz a leitura de planilhas XLSX, XLS e CSV e converte as linhas para Objetos em Java.

Foi baseada em outra lib excelr, porém com algumas modificações úteis nas regras de leitura.

Examplo de uso:

		RowConverter<Country> converter = (Object[] row) -> new Country((String)row[0], (String)row[1]);
		
		ExcelReader<Country> reader = ExcelReader.builder(Country.class)
		     .converter(converter)
		     .withHeader()
		     .csvDelimiter(';')
		     .sheets(1)
		     .build();
		
		List<Country> list;
		list = reader.read("src/test/resources/CountryCodes.xlsx");
		list = reader.read("src/test/resources/CountryCodes.xls");
		list = reader.read("src/test/resources/CountryCodes.csv");
