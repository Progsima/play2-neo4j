// create a unique constraint on name for content type.
CREATE CONSTRAINT ON (content:Content_Type) ASSERT content.name IS UNIQUE;

