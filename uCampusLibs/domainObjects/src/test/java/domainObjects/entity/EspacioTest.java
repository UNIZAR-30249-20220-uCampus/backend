package domainObjects.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EspacioTest {
	@Test
	public void crearEspacio() {
		Espacio espacio = new Espacio();
		assertNotNull(espacio);
	}

	@Test
	public void crearEspacioConEquipamientosMaximos() {
		Espacio espacio = new Espacio();
		assertNotNull(espacio);
		assertNotNull(espacio.getMaximoDeEquipamientos());
	}
}