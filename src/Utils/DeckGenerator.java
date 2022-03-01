package Utils;

import org.json.JSONArray;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckGenerator {

  public List<Color> generate() {
    List<Color> res = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      Color color;
      switch (i) {
        case 0: color = Color.RED; break;
        case 1: color = Color.GREEN; break;
        case 2: color = Color.BLUE; break;
        case 3: color = Color.WHITE; break;
        default: color = Color.BLACK; break;
      }
      for (int j = 0; j < 62; j++) {
        res.add(color);
      }
    }
    res.add(Color.RED);
    res.add(Color.RED);

    Collections.shuffle(res);
    return res;
  }
}
