INSERT INTO kind_type_test SELECT * from kind_type 
INSERT INTO link_type_test SELECT * from link_type 
INSERT INTO info_type_test SELECT * from info_type 
INSERT INTO company_type_test SELECT * from company_type 
INSERT INTO role_type_test SELECT * from role_type

INSERT INTO title_test SELECT * from title WHERE kind_id IN(SELECT id FROM kind_type WHERE kind = 'movie' OR kind = 'tv series' )AND production_year between 1988 and 1990

INSERT INTO movie_link_test SELECT * from movie_link  WHERE movie_id IN(SELECT id FROM title_test WHERE id = movie_id) OR linked_movie_id IN(SELECT id FROM title_test WHERE id=linked_movie_id) 

INSERT INTO movie_info_idx_test SELECT * from movie_info_idx WHERE info_type_id IN(SELECT id FROM info_type WHERE info = 'birth date' OR info = 'death date' OR info = 'runtimes' OR info = 'genres' OR info = 'mpaa' OR info = 'votes' ) AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) 

INSERT INTO movie_info_test SELECT * from movie_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = 'birth date' OR info = 'death date' OR info = 'runtimes' OR info = 'genres' OR info = 'mpaa' OR info = 'votes' ) AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) 

INSERT INTO movie_companies_test SELECT * from movie_companies WHERE company_type_id IN(SELECT id FROM company_type WHERE kind = 'production companies' ) AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) 

INSERT INTO person_info_test SELECT * from person_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = 'birth date' OR info = 'death date' OR info = 'runtimes' OR info = 'genres' OR info = 'mpaa' OR info = 'votes' )

INSERT INTO cast_info_test SELECT * from cast_info WHERE role_id IN(SELECT id FROM role_type WHERE role = 'producer' OR role = 'director' ) AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) 

INSERT INTO company_name_test SELECT * from company_name WHERE id IN(SELECT company_id FROM movie_companies WHERE id=company_id) 

INSERT INTO name_test SELECT * from name WHERE id IN(SELECT person_role_id FROM cast_info WHERE id=person_role_id) 

INSERT INTO char_name_test SELECT * from char_name WHERE id IN(SELECT person_id FROM cast_info WHERE id=person_id) 

---------------------Korrigiert-----------------
INSERT INTO movie_link_test SELECT * from movie_link  WHERE movie_id IN(SELECT id FROM title_test

WHERE id = movie_id) AND linked_movie_id IN(SELECT id FROM title_test WHERE id=linked_movie_id)
-------------------------------noch nicht Korrigiert-------------------------------------------
INSERT INTO person_info_test SELECT * from person_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = 'birth date' OR info = 'death date' OR info = 'runtimes' OR info = 'genres' OR info = 'mpaa' OR info = 'votes' )
