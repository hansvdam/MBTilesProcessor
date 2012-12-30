DROP TABLE IF EXIST metadata;
CREATE TABLE metadata (name text, value text);
INSERT INTO metadata VALUES('bounds','$1');
INSERT INTO metadata VALUES('name','$2');
INSERT INTO metadata VALUES('type','baselayer');
INSERT INTO metadata VALUES('version',1.1);
INSERT INTO metadata VALUES('description','created by MbTilesMerger');
INSERT INTO metadata VALUES('format','png');
CREATE TABLE IF NOT EXIST tiles (zoom_level integer, tile_column integer, tile_row integer, tile_data blob);
