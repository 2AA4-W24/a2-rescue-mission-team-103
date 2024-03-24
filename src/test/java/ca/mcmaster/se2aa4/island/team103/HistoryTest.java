package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team103.history.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class HistoryTest {
	
	// Additional tests to ensure that history is working beyond what has already been tested in other classes. 
	@Test
	void getItemResp(){
		ResponseHistory hist = new ResponseHistory();
		hist.addItem(new JSONObject());
		assertEquals(hist.getItem(0).toString(),new JSONObject().toString());
	}

	@Test
	void RespOffset(){
		ResponseHistory hist = new ResponseHistory();
		hist.addItem(new JSONObject());
		hist.addItem(new JSONObject());
		List<JSONObject> expected = new ArrayList<JSONObject>();
		expected.add(new JSONObject());
		expected.add(new JSONObject());
		assertEquals(hist.getItems(0).toString(),expected.toString());
	}

	@Test
	void RespSize(){
		ResponseHistory hist = new ResponseHistory();
		hist.addItem(new JSONObject());
		hist.addItem(new JSONObject());
		assertEquals(hist.getSize(),2);
	}

	@Test
	void getItemNav(){
		NavHistory hist = new NavHistory();
		hist.addItem(new Coordinate(0,0));
		Coordinate expected = new Coordinate(0,0);
		assertTrue(expected.equalTo(hist.getItem(0)));
		assertTrue(expected.equalTo(hist.getFirst()));
		assertTrue(expected.equalTo(hist.getLast()));
	}

	@Test
	void NavOffset(){
		NavHistory hist = new NavHistory();
		hist.addItem(new Coordinate(0,0));
		hist.addItem(new Coordinate(0,0));
		List<Coordinate> expected = new ArrayList<Coordinate>();
		expected.add(new Coordinate(0,0));
		expected.add(new Coordinate(0,0));
		assertEquals(hist.getItems(0).toString(),expected.toString());
		assertEquals(hist.getItems(-2).toString(),expected.toString());
	}

	@Test
	void NavSize(){
		NavHistory hist = new NavHistory();
		hist.addItem(new Coordinate(0,0));
		hist.addItem(new Coordinate(5,5));
		assertEquals(hist.getSize(),2);
	}
}
