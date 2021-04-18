DO $$
    DECLARE
        tabnames RECORD;
    BEGIN
        FOR tabnames IN (SELECT tablename FROM pg_tables
                        WHERE schemaname = current_schema()
                        AND tablename like 'pr_%')
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(tabnames.tablename) || ' CASCADE';
    END LOOP;
END $$;

DROP TYPE IF EXISTS pr_sex;
