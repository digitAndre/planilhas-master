package co.elron.excelr;

import br.com.utils.planilhas.ExcelReader;
import br.com.utils.planilhas.ExcelReader.RowConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExcelTest {

	public static class Country {
		public String shortCode;
		public String name;

		public Country(String shortCode, String name) {
			this.shortCode = shortCode;
			this.name = name;
		}
	}

	interface Run {
		void run() throws Exception;
	}

	private ExcelReader<Country> reader;

	@Before
	public void setUp() {
		RowConverter<Country> converter = (row) -> new Country((String) row[0], (String) row[1]);
		reader = ExcelReader.builder(Country.class)
				            .converter(converter)
				            .comCabecalho()
				            .delimitadorCsv(';')
				            .aba("Sheet2")
				            .build();
	}

	@Test
	public void shouldParseCorrectly_GivenXlsxFile() throws Exception {
		List<Country> list;
		list = reader.read("src/test/resources/CountryCodes.xlsx");
		checkList(list);
	}

	@Test
	public void shouldParseCorrectly_GivenXlsFile() throws Exception {
		List<Country> list;
		list = reader.read("src/test/resources/CountryCodes.xls");
		checkList(list);
	}

	@Test
	public void shouldParseCorrectly_GivenCsvFile() throws Exception {
		List<Country> list;
		list = reader.read("src/test/resources/CountryCodes.csv");
		checkList(list);
	}

	@Test
	public void shouldHandleNullValues_GivenANullCell() throws Exception {
		List<Country> list;
		list = reader.read("src/test/resources/CountryCodes.xls");
		Country country = list.get(1);
		assertNull(country.name);
		assertEquals("ae", country.shortCode);
	}

	private void checkList(List<Country> list) {
		assertEquals(252, list.size());
		assertEquals(list.get(0).shortCode, "ad");
		assertEquals(list.get(0).name, "Andorra");

		assertEquals(list.get(56).shortCode, "dj");
		assertEquals(list.get(56).name, "Djibouti");

		assertEquals(list.get(243).shortCode, "wf");
		assertEquals(list.get(243).name, "Wallis and Futuna Islands");
	}

	@Test
	public void benchmark() throws Exception {
		RowConverter<Country> converter = (row) -> new Country((String) row[0], (String) row[1]);

		ExcelReader<Country> reader = ExcelReader.builder(Country.class)
				.converter(converter)
				.delimitadorCsv(';')
				.aba("Sheet1")
				.comCabecalho()
				.build();

		delta(() -> reader.read("src/test/resources/CountryCodes.xlsx"));
		delta(() -> reader.read("src/test/resources/CountryCodes.xls"));
		delta(() -> reader.read("src/test/resources/CountryCodes.csv"));
	}

	public void delta(Run c) throws Exception {
		long start = System.currentTimeMillis();
		c.run();
		System.out.println("Delta: " + (System.currentTimeMillis() - start));
	}
}
