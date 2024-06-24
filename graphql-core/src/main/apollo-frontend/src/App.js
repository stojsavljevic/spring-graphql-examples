import { useQuery, useSubscription, gql } from '@apollo/client';

const GET_POSTS = gql`
  query {
    allPosts {
      id
      title
      content
      releaseYear
      author {
        name
      }
    }
  }
`;

const SUB_POST = gql`
  subscription {
    randomPost {
      title
      content
      releaseYear
      author {
        name
      }
    }
  }
`

function GetAllPosts() {
  const { loading, error, data } = useQuery(GET_POSTS);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error : {error.message}</p>;

  return data.allPosts.map(({ id, title, content, author }) => (
    <p key={id}><b>{author.name}</b>: <i>{title}</i> - {content} </p>
  ));
}

function GetRandomPost() {
  const { loading, error, data } = useSubscription(SUB_POST);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error : {error.message}</p>;

  return  <div>
            <p><b>{data.randomPost.author.name}:</b></p>
            <p><i>{data.randomPost.title}</i></p>
            <p>{data.randomPost.content}</p>
            <p>{data.randomPost.releaseYear}</p>
          </div>;
}

export default function App() {
  return (
    <div>
      <h1>Spring GraphQL Examples with React 🤓️</h1>
      <p>This page uses Apollo client that leverages graphql-ws library and graphql-transport-ws protocol for subscriptions</p>
      <br/>
      <h2>All Posts Query</h2>
      <GetAllPosts />
      <br/>
      <h2>Random Post Subscription</h2>
      <GetRandomPost />
    </div>
  );
}