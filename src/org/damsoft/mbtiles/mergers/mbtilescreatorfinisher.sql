DROP INDEX IF EXISTS metadata_idx;
DROP INDEX IF EXISTS tiles_idx;
CREATE UNIQUE INDEX metadata_idx  ON metadata (name);
CREATE INDEX tiles_idx on tiles (zoom_level, tile_column, tile_row);