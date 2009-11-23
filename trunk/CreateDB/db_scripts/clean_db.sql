delete from libavtor where BookId in (select BookId from libbook where not FileType='fb2' or Deleted=1 or Blocked=1);
delete from libbook where not FileType='fb2' or Deleted=1 or Blocked=1;
delete from libavtorname where not AvtorId in (select AvtorId from libavtor);
vacuum;