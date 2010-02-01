delete from libavtor where BookId in (select BookId from libbook where not FileType='fb2' or Deleted=1 or Blocked=1);
delete from libbook where not FileType='fb2' or Deleted=1 or Blocked=1;
delete from libavtorname where not AvtorId in (select AvtorId from libavtor);
delete from libaannotations where not AvtorId in (select AvtorId from libavtor);
delete from libapics where not AvtorId in (select AvtorId from libavtor);
delete from libbannotations where not BookId in (select BookId from libbook);
delete from libbpics where not BookId in (select BookId from libbook);
vacuum;